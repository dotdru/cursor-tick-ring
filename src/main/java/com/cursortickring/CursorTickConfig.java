package com.cursortickring;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(CursorTickConfig.GROUP)
public interface CursorTickConfig extends Config
{
	String GROUP = "cursor-tick-ring";

	@Range(min = 6, max = 100)
	@ConfigItem(
		keyName = "radius",
		name = "Radius",
		description = "Circle Radius",
		position = 0
	)
	default int radius()
	{
		return 22;
	}

	@Range(min = 1, max = 20)
	@ConfigItem(
		keyName = "thickness",
		name = "Thickness",
		description = "Thickness",
		position = 1
	)
	default int thickness()
	{
		return 4;
	}

	@Alpha
	@ConfigItem(
		keyName = "progressColor",
		name = "Progress color",
		description = "Progress Color",
		position = 2
	)
	default Color progressColor()
	{
		return new Color(0, 255, 170, 235);
	}

	@ConfigItem(
		keyName = "showTrack",
		name = "Background track",
		description = "Show Background",
		position = 3
	)
	default boolean showTrack()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "trackColor",
		name = "Track color",
		description = "Background Color",
		position = 4
	)
	default Color trackColor()
	{
		return new Color(0, 0, 0, 125);
	}
}