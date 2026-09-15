package com.cursortickring;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(CursorTickConfig.GROUP)
public interface CursorTickConfig extends Config
{
	String GROUP = "cursor-tick-ring-training";

	@ConfigSection(
		name = "Tick ring",
		description = "Appearance of the game-tick progress ring",
		position = 0
	)
	String RING_SECTION = "ring";

	@ConfigItem(
		keyName = "ringStyle",
		name = "Style",
		description = "Fill, drain, or rotate the active part of the ring",
		section = RING_SECTION,
		position = 0
	)
	default RingStyle ringStyle()
	{
		return RingStyle.FILL;
	}

	@ConfigItem(
		keyName = "rotationDirection",
		name = "Direction",
		description = "Direction in which progress moves",
		section = RING_SECTION,
		position = 1
	)
	default RotationDirection rotationDirection()
	{
		return RotationDirection.CLOCKWISE;
	}

	@Range(min = 0, max = 359)
	@ConfigItem(
		keyName = "startAngle",
		name = "Start angle",
		description = "Starting point in clockwise degrees from 12 o'clock",
		section = RING_SECTION,
		position = 2
	)
	default int startAngle()
	{
		return 0;
	}

	@Range(min = 6, max = 100)
	@ConfigItem(
		keyName = "radius",
		name = "Radius",
		description = "Distance from the cursor center to the ring, in pixels",
		section = RING_SECTION,
		position = 3
	)
	default int radius()
	{
		return 22;
	}

	@Range(min = 1, max = 20)
	@ConfigItem(
		keyName = "thickness",
		name = "Thickness",
		description = "Width of the main ring, in pixels",
		section = RING_SECTION,
		position = 4
	)
	default int thickness()
	{
		return 4;
	}

	@Alpha
	@ConfigItem(
		keyName = "progressColor",
		name = "Progress color",
		description = "Color and opacity of the active arc",
		section = RING_SECTION,
		position = 5
	)
	default Color progressColor()
	{
		return new Color(0, 255, 170, 235);
	}

	@ConfigItem(
		keyName = "showTrack",
		name = "Background track",
		description = "Draw a full ring behind the active arc",
		section = RING_SECTION,
		position = 6
	)
	default boolean showTrack()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "trackColor",
		name = "Track color",
		description = "Color and opacity of the background track",
		section = RING_SECTION,
		position = 7
	)
	default Color trackColor()
	{
		return new Color(0, 0, 0, 125);
	}

	@Range(min = 5, max = 270)
	@ConfigItem(
		keyName = "sweepSize",
		name = "Sweep size",
		description = "Arc length in degrees for the rotating sweep style",
		section = RING_SECTION,
		position = 8
	)
	default int sweepSize()
	{
		return 75;
	}
}