package com.cursortickring;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Cursor Tick Ring",
	description = "GCD-style cursor tick ring",
	tags = {"tick", "cursor", "timing", "metronome", "overlay"}
)

public class CursorTickPlugin extends Plugin
{
	@Inject
	private CursorTickOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	private final TickClock tickClock = new TickClock();

	@Override
	protected void startUp()
	{
		tickClock.reset();
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		tickClock.reset();
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		tickClock.onGameTick(System.nanoTime());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOGGED_IN)
		{
			tickClock.reset();
		}
	}

	TickClock.State getTickState()
	{
		return tickClock.getState();
	}

	@Provides
	CursorTickConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CursorTickConfig.class);
	}
}