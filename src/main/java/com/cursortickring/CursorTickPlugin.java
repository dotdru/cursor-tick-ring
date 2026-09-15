package com.cursortickring;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
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

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}

	@Provides
	CursorTickConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CursorTickConfig.class);
	}
}