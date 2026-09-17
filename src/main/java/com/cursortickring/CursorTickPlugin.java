package com.cursortickring;

import com.google.inject.Provides;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedDeque;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Player;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStats;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseAdapter;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

@PluginDescriptor(
	name = "Cursor Tick Ring",
	description = "Shows smooth, game-tick-synced progress around the mouse cursor",
	tags = {"tick", "cursor", "timing", "metronome", "overlay"}
)
public class CursorTickPlugin extends Plugin
{
	private static final int MAX_CLICK_PULSES = 8;
	private static final int MILLIS_PER_SECOND = 1000;

	@Inject
	private Client client;

	@Inject
	private CursorTickConfig config;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ConfigManager configManager;

	@Inject
	private CursorTickOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private KeyManager keyManager;

	private final TickClock tickClock = new TickClock();
	private final TickCycle tickCycle = new TickCycle();
	private final AttackTickCounter attackCounter = new AttackTickCounter();
	private final ConcurrentLinkedDeque<ClickPulse> clickPulses = new ConcurrentLinkedDeque<>();

	private volatile long lastMouseActivityNanos;
	private volatile boolean overlayEnabled = true;

	/*
	 * These fields are only read or changed on Swing's event-dispatch thread.
	 */
	private Cursor hiddenCursor;
	private Cursor previousCursor;
	private boolean previousCursorWasSet;
	private boolean nativeCursorHidden;

	private final MouseAdapter mouseListener = new MouseAdapter()
	{
		@Override
		public MouseEvent mousePressed(MouseEvent mouseEvent)
		{
			long nowNanos = System.nanoTime();
			lastMouseActivityNanos = nowNanos;

			if (overlayEnabled && config.showClickPulse())
			{
				while (clickPulses.size() >= MAX_CLICK_PULSES)
				{
					clickPulses.pollFirst();
				}

				clickPulses.addLast(new ClickPulse(
					nowNanos,
					mouseEvent.getX(),
					mouseEvent.getY(),
					mouseEvent.getButton()));
			}

			return mouseEvent;
		}

		@Override
		public MouseEvent mouseMoved(MouseEvent mouseEvent)
		{
			lastMouseActivityNanos = System.nanoTime();
			return mouseEvent;
		}

		@Override
		public MouseEvent mouseDragged(MouseEvent mouseEvent)
		{
			lastMouseActivityNanos = System.nanoTime();
			return mouseEvent;
		}

		@Override
		public MouseEvent mouseEntered(MouseEvent mouseEvent)
		{
			lastMouseActivityNanos = System.nanoTime();
			return mouseEvent;
		}
	};

	private final HotkeyListener restartCycleHotkeyListener =
		new HotkeyListener(() -> config.restartCycleHotkey())
		{
			@Override
			public void hotkeyPressed()
			{
				tickCycle.restartAt(tickClock.getState().getTickNumber());
			}
		};

	private final HotkeyListener toggleOverlayHotkeyListener =
		new HotkeyListener(() -> config.toggleOverlayHotkey())
		{
			@Override
			public void hotkeyPressed()
			{
				overlayEnabled = !overlayEnabled;
				clickPulses.clear();

				if (overlayEnabled)
				{
					lastMouseActivityNanos = System.nanoTime();
				}

				updateNativeCursor();
			}
		};

	@Override
	protected void startUp()
	{
		migrateSettings();
		overlayEnabled = true;
		lastMouseActivityNanos = System.nanoTime();
		tickClock.reset(config.tickDuration());
		tickCycle.reset();
		attackCounter.reset();
		clickPulses.clear();

		mouseManager.registerMouseListener(mouseListener);
		keyManager.registerKeyListener(restartCycleHotkeyListener);
		keyManager.registerKeyListener(toggleOverlayHotkeyListener);
		overlayManager.add(overlay);
		updateNativeCursor();
	}

	@Override
	protected void shutDown()
	{
		overlayEnabled = false;
		overlayManager.remove(overlay);
		keyManager.unregisterKeyListener(toggleOverlayHotkeyListener);
		keyManager.unregisterKeyListener(restartCycleHotkeyListener);
		mouseManager.unregisterMouseListener(mouseListener);

		clickPulses.clear();
		attackCounter.reset();
		tickCycle.reset();
		tickClock.reset(config.tickDuration());
		restoreNativeCursor();
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		tickClock.onGameTick(
			System.nanoTime(),
			config.tickDuration(),
			config.timingMode(),
			config.adaptationStrength());

		if (config.attackTimerMode())
		{
			observeAttackAnimation();
			attackCounter.onGameTick();
		}
		else
		{
			attackCounter.reset();
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if (config.attackTimerMode())
		{
			observeAttackAnimation();
		}
	}

	private void observeAttackAnimation()
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			attackCounter.reset();
			return;
		}

		Player player = client.getLocalPlayer();
		if (player == null || player.isDead())
		{
			attackCounter.reset();
			return;
		}

		if (!attackCounter.observeAnimation(player.getAnimation(), player.getAnimationFrame()))
		{
			return;
		}

		AttackAnimations.Kind kind = AttackAnimations.classify(player.getAnimation());
		if (kind != AttackAnimations.Kind.NONE)
		{
			boolean rapid = client.getVarpValue(VarPlayerID.COM_MODE) == 1;
			attackCounter.recordAttack(kind.period(getWeaponAttackSpeed(), rapid));
		}
	}

	private int getWeaponAttackSpeed()
	{
		ItemContainer equipment = client.getItemContainer(InventoryID.WORN);
		Item weapon = equipment == null ? null : equipment.getItem(EquipmentInventorySlot.WEAPON.getSlotIdx());
		if (weapon == null || weapon.getId() < 0)
		{
			return 0;
		}

		ItemStats stats = itemManager.getItemStats(weapon.getId());
		return stats == null || stats.getEquipment() == null ? 0 : stats.getEquipment().getAspeed();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			tickClock.reset(config.tickDuration());
			tickCycle.reset();
			attackCounter.reset();
			clickPulses.clear();
		}

		updateNativeCursor();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (CursorTickConfig.GROUP.equals(event.getGroup()))
		{
			if ("attackTimerMode".equals(event.getKey()))
			{
				attackCounter.reset();
			}
			updateNativeCursor();
		}
	}

	TickClock.State getTickState()
	{
		return tickClock.getState();
	}

	int getAttackTicksRemaining()
	{
		return attackCounter.getRemaining();
	}

	int getCyclePosition(long tickNumber, int cycleLength)
	{
		return tickCycle.positionAt(tickNumber, cycleLength);
	}

	Iterable<ClickPulse> getClickPulses()
	{
		return clickPulses;
	}

	void removeClickPulse(ClickPulse clickPulse)
	{
		clickPulses.remove(clickPulse);
	}

	long getLastMouseActivityNanos()
	{
		return lastMouseActivityNanos;
	}

	boolean isOverlayEnabled()
	{
		return overlayEnabled;
	}

	boolean isCustomCursorActive()
	{
		return overlayEnabled
			&& config.cursorMode().hidesSystemCursor()
			&& (config.visibilityMode() == VisibilityMode.ALWAYS
				|| client.getGameState() == GameState.LOGGED_IN);
	}

	private void migrateSettings()
	{
		migrateMillisecondsSetting("idleFadeDelay", "idleFadeDelaySeconds");
		migrateMillisecondsSetting("idleFadeDuration", "idleFadeDurationSeconds");
		migrateSetting("softMotionTrail", "tickResetFade");
	}

	private void migrateSetting(String oldKey, String newKey)
	{
		String oldValue = configManager.getConfiguration(CursorTickConfig.GROUP, oldKey);
		String newValue = configManager.getConfiguration(CursorTickConfig.GROUP, newKey);
		if (oldValue == null || newValue != null)
		{
			return;
		}

		configManager.setConfiguration(CursorTickConfig.GROUP, newKey, oldValue);
		configManager.unsetConfiguration(CursorTickConfig.GROUP, oldKey);
	}

	private void migrateMillisecondsSetting(String oldKey, String newKey)
	{
		String oldValue = configManager.getConfiguration(CursorTickConfig.GROUP, oldKey);
		String newValue = configManager.getConfiguration(CursorTickConfig.GROUP, newKey);
		if (oldValue == null || newValue != null)
		{
			return;
		}

		try
		{
			int milliseconds = Integer.parseInt(oldValue);
			int seconds = milliseconds <= 0
				? 0
				: Math.max(1, (milliseconds + MILLIS_PER_SECOND - 1) / MILLIS_PER_SECOND);
			configManager.setConfiguration(CursorTickConfig.GROUP, newKey, seconds);
			configManager.unsetConfiguration(CursorTickConfig.GROUP, oldKey);
		}
		catch (NumberFormatException ignored)
		{
		}
	}

	private void updateNativeCursor()
	{
		boolean shouldHide = isCustomCursorActive();
		SwingUtilities.invokeLater(() -> setNativeCursorHidden(shouldHide));
	}

	private void restoreNativeCursor()
	{
		SwingUtilities.invokeLater(() -> setNativeCursorHidden(false));
	}

	private void setNativeCursorHidden(boolean shouldHide)
	{
		if (shouldHide == nativeCursorHidden)
		{
			return;
		}

		if (shouldHide)
		{
			Cursor transparentCursor = getHiddenCursor();
			if (transparentCursor == null)
			{
				return;
			}

			Component clientComponent = (Component) client;
			previousCursorWasSet = clientComponent.isCursorSet();
			previousCursor = clientComponent.getCursor();
			clientComponent.setCursor(transparentCursor);
			nativeCursorHidden = true;
			return;
		}

		Component clientComponent = (Component) client;
		if (clientComponent.getCursor() == hiddenCursor)
		{
			clientComponent.setCursor(previousCursorWasSet
				? previousCursor
				: null);
		}

		previousCursor = null;
		previousCursorWasSet = false;
		nativeCursorHidden = false;
	}

	private Cursor getHiddenCursor()
	{
		if (hiddenCursor != null)
		{
			return hiddenCursor;
		}

		try
		{
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Dimension size = toolkit.getBestCursorSize(16, 16);
			if (size.width <= 0 || size.height <= 0)
			{
				return null;
			}

			BufferedImage image = new BufferedImage(
				size.width,
				size.height,
				BufferedImage.TYPE_INT_ARGB);
			hiddenCursor = toolkit.createCustomCursor(
				image,
				new java.awt.Point(0, 0),
				"cursor-tick-ring-hidden");
			return hiddenCursor;
		}
		catch (RuntimeException ignored)
		{
			return null;
		}
	}

	@Provides
	CursorTickConfig provideConfig(ConfigManager manager)
	{
		return manager.getConfig(CursorTickConfig.class);
	}
}
