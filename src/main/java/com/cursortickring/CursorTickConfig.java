package com.cursortickring;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Range;

@ConfigGroup(CursorTickConfig.GROUP)
public interface CursorTickConfig extends Config
{
	String GROUP = "cursor-tick-ring";

	@ConfigSection(
		name = "General",
		description = "Visibility, placement, and rendering behavior",
		position = 0
	)
	String GENERAL_SECTION = "general";

	@ConfigSection(
		name = "Cursor",
		description = "Optional replacement for the native system cursor",
		position = 1,
		closedByDefault = true
	)
	String CURSOR_SECTION = "cursor";

	@ConfigSection(
		name = "Tick ring",
		description = "The main tick-progress ring",
		position = 2
	)
	String RING_SECTION = "ring";

	@ConfigSection(
		name = "Tick label",
		description = "Tick number, shared cycle length, and cycle-start color",
		position = 3,
		closedByDefault = true
	)
	String LABEL_SECTION = "label";

	@ConfigSection(
		name = "Click feedback",
		description = "Optional rings shown when a mouse button is pressed",
		position = 4,
		closedByDefault = true
	)
	String CLICK_SECTION = "click";

	@ConfigSection(
		name = "Controls",
		description = "Optional keyboard shortcuts; hotkeys are unset by default",
		position = 5,
		closedByDefault = true
	)
	String CONTROLS_SECTION = "controls";

	@ConfigSection(
		name = "Timing",
		description = "Advanced display timing. These settings only control interpolation and visual feedback",
		position = 6,
		closedByDefault = true
	)
	String TIMING_SECTION = "timing";

	@ConfigItem(
		keyName = "attackTimerMode",
		name = "Attack timer mode (experimental)",
		description = "Show estimated attack ticks left; replaces normal tick-label controls. Food delay not included",
		section = GENERAL_SECTION,
		position = -1
	)
	default boolean attackTimerMode()
	{
		return false;
	}

	@ConfigItem(
		keyName = "visibilityMode",
		name = "Visibility",
		description = "Choose whether the ring also appears outside the logged-in game state",
		section = GENERAL_SECTION,
		position = 0
	)
	default VisibilityMode visibilityMode()
	{
		return VisibilityMode.LOGGED_IN;
	}

	@ConfigItem(
		keyName = "gameViewportOnly",
		name = "Game viewport only",
		description = "Hide the ring while the cursor is over side panels, chat, or other interfaces",
		section = GENERAL_SECTION,
		position = 1
	)
	default boolean gameViewportOnly()
	{
		return false;
	}

	@ConfigItem(
		keyName = "hideWhenMenuOpen",
		name = "Hide on right-click menu",
		description = "Hide the tick ring while the context menu is open",
		section = GENERAL_SECTION,
		position = 2
	)
	default boolean hideWhenMenuOpen()
	{
		return true;
	}

	@ConfigItem(
		keyName = "horizontalOffset",
		name = "Horizontal offset",
		description = "Move the ring left or right from the cursor, in pixels",
		section = GENERAL_SECTION,
		position = 3
	)
	@Range(min = -100, max = 100)
	default int horizontalOffset()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "verticalOffset",
		name = "Vertical offset",
		description = "Move the ring up or down from the cursor, in pixels",
		section = GENERAL_SECTION,
		position = 4
	)
	@Range(min = -100, max = 100)
	default int verticalOffset()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "idleFadeDelaySeconds",
		name = "Idle after (seconds)",
		description = "Start fading after this many whole seconds without mouse movement; 0 disables idle fading",
		section = GENERAL_SECTION,
		position = 5
	)
	@Range(min = 0, max = 300)
	default int idleFadeDelaySeconds()
	{
		return 10;
	}

	@ConfigItem(
		keyName = "idleFadeDurationSeconds",
		name = "Fade time (seconds)",
		description = "Whole seconds taken to fade completely after becoming idle; 0 hides immediately",
		section = GENERAL_SECTION,
		position = 6
	)
	@Range(min = 0, max = 30)
	default int idleFadeDurationSeconds()
	{
		return 1;
	}

	@ConfigItem(
		keyName = "antiAliasing",
		name = "Smooth edges",
		description = "Use anti-aliasing for smoother rings and text",
		section = GENERAL_SECTION,
		position = 7
	)
	default boolean antiAliasing()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFeedbackOutline",
		name = "Feedback outlines",
		description = "Draw an outline around pulse-on-tick and mouse-click feedback rings",
		section = GENERAL_SECTION,
		position = 8
	)
	default boolean showFeedbackOutline()
	{
		return true;
	}

	@ConfigItem(
		keyName = "cursorMode",
		name = "Cursor",
		description = "Keep the system cursor, replace it with a dot or crosshair, or hide it without drawing a marker",
		section = CURSOR_SECTION,
		position = 0
	)
	default CursorMode cursorMode()
	{
		return CursorMode.SYSTEM;
	}

	@Range(min = 3, max = 40)
	@ConfigItem(
		keyName = "cursorMarkerSize",
		name = "Marker size",
		description = "Diameter of the dot or total width and height of the crosshair, in pixels",
		section = CURSOR_SECTION,
		position = 1
	)
	default int cursorMarkerSize()
	{
		return 5;
	}

	@Range(min = 1, max = 8)
	@ConfigItem(
		keyName = "cursorMarkerThickness",
		name = "Marker thickness",
		description = "Line width of the crosshair; the dot remains filled",
		section = CURSOR_SECTION,
		position = 2
	)
	default int cursorMarkerThickness()
	{
		return 1;
	}

	@Range(min = 0, max = 12)
	@ConfigItem(
		keyName = "cursorMarkerGap",
		name = "Crosshair gap",
		description = "Empty space between the cursor position and each crosshair arm, in pixels",
		section = CURSOR_SECTION,
		position = 3
	)
	default int cursorMarkerGap()
	{
		return 2;
	}

	@Alpha
	@ConfigItem(
		keyName = "cursorMarkerColor",
		name = "Marker color",
		description = "Color and opacity of the replacement cursor marker",
		section = CURSOR_SECTION,
		position = 4
	)
	default Color cursorMarkerColor()
	{
		return new Color(255, 255, 255, 255);
	}

	@ConfigItem(
		keyName = "cursorMarkerOutline",
		name = "Marker outline",
		description = "Draw a contrasting outline around the dot or crosshair",
		section = CURSOR_SECTION,
		position = 5
	)
	default boolean cursorMarkerOutline()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "cursorMarkerOutlineColor",
		name = "Marker outline color",
		description = "Color and opacity of the replacement cursor outline",
		section = CURSOR_SECTION,
		position = 6
	)
	default Color cursorMarkerOutlineColor()
	{
		return new Color(0, 0, 0, 220);
	}

	@ConfigItem(
		keyName = "ringStyle",
		name = "Style",
		description = "Fill, drain, or rotate the active part of the ring each game tick",
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
		description = "Direction in which tick progress moves",
		section = RING_SECTION,
		position = 1
	)
	default RotationDirection rotationDirection()
	{
		return RotationDirection.CLOCKWISE;
	}

	@ConfigItem(
		keyName = "startAngle",
		name = "Start angle",
		description = "Starting point in clockwise degrees from 12 o'clock",
		section = RING_SECTION,
		position = 2
	)
	@Range(min = 0, max = 359)
	default int startAngle()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "radius",
		name = "Radius",
		description = "Distance from the cursor center to the ring, in pixels",
		section = RING_SECTION,
		position = 3
	)
	@Range(min = 6, max = 100)
	default int radius()
	{
		return 14;
	}

	@ConfigItem(
		keyName = "thickness",
		name = "Thickness",
		description = "Width of the main ring, in pixels",
		section = RING_SECTION,
		position = 4
	)
	@Range(min = 1, max = 20)
	default int thickness()
	{
		return 2;
	}

	@ConfigItem(
		keyName = "lineCap",
		name = "Line ends",
		description = "Shape of arc and segment ends",
		section = RING_SECTION,
		position = 5
	)
	default LineCap lineCap()
	{
		return LineCap.ROUND;
	}

	@Alpha
	@ConfigItem(
		keyName = "progressColor",
		name = "Progress color",
		description = "Color and opacity of the active tick arc",
		section = RING_SECTION,
		position = 6
	)
	default Color progressColor()
	{
		return new Color(0, 255, 170, 235);
	}

	@ConfigItem(
		keyName = "showTrack",
		name = "Background track",
		description = "Draw a full ring behind the active tick arc",
		section = RING_SECTION,
		position = 7
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
		position = 8
	)
	default Color trackColor()
	{
		return new Color(0, 0, 0, 125);
	}

	@ConfigItem(
		keyName = "showOutline",
		name = "Outline",
		description = "Draw a contrasting outline around ring strokes",
		section = RING_SECTION,
		position = 9
	)
	default boolean showOutline()
	{
		return true;
	}

	@Range(min = 1, max = 5)
	@ConfigItem(
		keyName = "outlineWidth",
		name = "Outline width",
		description = "Extra outline width around each side of a ring stroke",
		section = RING_SECTION,
		position = 10
	)
	default int outlineWidth()
	{
		return 1;
	}

	@Alpha
	@ConfigItem(
		keyName = "outlineColor",
		name = "Outline color",
		description = "Color and opacity of ring outlines",
		section = RING_SECTION,
		position = 11
	)
	default Color outlineColor()
	{
		return new Color(0, 0, 0, 210);
	}

	@Range(min = 1, max = 24)
	@ConfigItem(
		keyName = "segments",
		name = "Segments",
		description = "Split the ring into this many equal segments; 1 draws a continuous ring",
		section = RING_SECTION,
		position = 12
	)
	default int segments()
	{
		return 1;
	}

	@Range(min = 0, max = 30)
	@ConfigItem(
		keyName = "segmentGap",
		name = "Segment gap",
		description = "Gap between segments in degrees",
		section = RING_SECTION,
		position = 13
	)
	default int segmentGap()
	{
		return 4;
	}

	@Range(min = 5, max = 270)
	@ConfigItem(
		keyName = "sweepSize",
		name = "Sweep size",
		description = "Arc length in degrees when using the rotating sweep style",
		section = RING_SECTION,
		position = 14
	)
	default int sweepSize()
	{
		return 75;
	}

	@ConfigItem(
		keyName = "showLeadingDot",
		name = "Leading marker",
		description = "Draw a dot at the moving edge of the active arc",
		section = RING_SECTION,
		position = 15
	)
	default boolean showLeadingDot()
	{
		return false;
	}

	@Range(min = 2, max = 12)
	@ConfigItem(
		keyName = "leadingDotSize",
		name = "Marker size",
		description = "Diameter of the leading marker, in pixels",
		section = RING_SECTION,
		position = 16
	)
	default int leadingDotSize()
	{
		return 6;
	}

	@ConfigItem(
		keyName = "tickResetFade",
		name = "Fade on tick reset",
		description = "Fade the completed ring after each tick in Fill style. Disabled in Sweep and Remaining styles",
		section = RING_SECTION,
		position = 17
	)
	default boolean fadeOnTickReset()
	{
		return true;
	}

	@Range(min = 25, max = 600)
	@ConfigItem(
		keyName = "tickResetFadeDuration",
		name = "Reset fade time (ms)",
		description = "How long the completed ring takes to fade after a real GameTick resets progress to zero",
		section = RING_SECTION,
		position = 18
	)
	default int tickResetFadeDuration()
	{
		return 180;
	}

	@ConfigItem(
		keyName = "timingMode",
		name = "Timing mode",
		description = "Fixed uses the exact configured duration. Adaptive blends recent valid GameTick intervals while still resetting on every real GameTick",
		section = TIMING_SECTION,
		position = 0
	)
	default TimingMode timingMode()
	{
		return TimingMode.FIXED;
	}

	@Range(min = 400, max = 1000)
	@ConfigItem(
		keyName = "tickDuration",
		name = "Tick duration (ms)",
		description = "Expected time between ticks. Old School RuneScape normally uses 600 ms; this changes only the animation between GameTick events",
		section = TIMING_SECTION,
		position = 1
	)
	default int tickDuration()
	{
		return 600;
	}

	@Range(min = 1, max = 100)
	@ConfigItem(
		keyName = "adaptationStrength",
		name = "Adaptation strength",
		description = "Percentage of each valid measured interval applied to the next animation. Higher values react faster; used only in Adaptive mode",
		section = TIMING_SECTION,
		position = 2
	)
	default int adaptationStrength()
	{
		return 20;
	}

	@Range(min = -250, max = 250)
	@ConfigItem(
		keyName = "phaseOffset",
		name = "Phase offset (ms)",
		description = "Display-only calibration. Positive values draw ahead and negative values draw behind; this does not change GameTick events",
		section = TIMING_SECTION,
		position = 3
	)
	default int phaseOffset()
	{
		return 0;
	}

	@ConfigItem(
		keyName = "showTickFlash",
		name = "Pulse on tick",
		description = "Draw a short expanding pulse when the selected position in the shared tick cycle begins",
		section = TIMING_SECTION,
		position = 4
	)
	default boolean pulseOnTick()
	{
		return false;
	}

	@Range(min = 0, max = 32)
	@ConfigItem(
		keyName = "pulseCycleTick",
		name = "Pulse cycle tick",
		description = "Cycle position that triggers the pulse. 0 pulses on every tick; cycle length is set in Tick label",
		section = TIMING_SECTION,
		position = 5
	)
	default int pulseCycleTick()
	{
		return 1;
	}

	@Range(min = 25, max = 500)
	@ConfigItem(
		keyName = "tickFlashDuration",
		name = "Pulse duration (ms)",
		description = "How long the pulse remains visible after its selected GameTick arrives",
		section = TIMING_SECTION,
		position = 6
	)
	default int pulseOnTickDuration()
	{
		return 110;
	}

	@Range(min = 0, max = 30)
	@ConfigItem(
		keyName = "tickFlashExpansion",
		name = "Pulse expansion",
		description = "Pixels added to the pulse radius over its lifetime",
		section = TIMING_SECTION,
		position = 7
	)
	default int pulseOnTickExpansion()
	{
		return 4;
	}

	@Alpha
	@ConfigItem(
		keyName = "tickFlashColor",
		name = "Pulse color",
		description = "Color and starting opacity of the pulse-on-tick ring",
		section = TIMING_SECTION,
		position = 8
	)
	default Color pulseOnTickColor()
	{
		return new Color(255, 255, 255, 210);
	}

	@ConfigItem(
		keyName = "showTickLabel",
		name = "Show tick label",
		description = "Display a tick number near the cursor",
		section = LABEL_SECTION,
		position = 0
	)
	default boolean showTickLabel()
	{
		return true;
	}

	@ConfigItem(
		keyName = "tickLabelMode",
		name = "Label value",
		description = "Show position in a repeating cycle, ticks remaining in it, or total ticks seen",
		section = LABEL_SECTION,
		position = 1
	)
	default TickLabelMode tickLabelMode()
	{
		return TickLabelMode.CYCLE_POSITION;
	}

	@Range(min = 1, max = 32)
	@ConfigItem(
		keyName = "cycleLength",
		name = "Cycle length",
		description = "Number of ticks shared by the label, cycle-start accent, selected pulse tick, and restart-cycle hotkey",
		section = LABEL_SECTION,
		position = 2
	)
	default int cycleLength()
	{
		return 4;
	}

	@ConfigItem(
		keyName = "tickLabelPosition",
		name = "Label position",
		description = "Place the tick label in the center, above, or below the ring",
		section = LABEL_SECTION,
		position = 3
	)
	default TickLabelPosition tickLabelPosition()
	{
		return TickLabelPosition.BELOW;
	}

	@Range(min = 8, max = 32)
	@ConfigItem(
		keyName = "labelFontSize",
		name = "Font size",
		description = "Tick label font size",
		section = LABEL_SECTION,
		position = 4
	)
	default int labelFontSize()
	{
		return 12;
	}

	@ConfigItem(
		keyName = "boldLabel",
		name = "Bold label",
		description = "Use bold text for the tick label",
		section = LABEL_SECTION,
		position = 5
	)
	default boolean boldLabel()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "labelColor",
		name = "Label color",
		description = "Color and opacity of the tick label",
		section = LABEL_SECTION,
		position = 6
	)
	default Color labelColor()
	{
		return new Color(255, 255, 255, 255);
	}

	@ConfigItem(
		keyName = "labelShadow",
		name = "Text shadow",
		description = "Draw a dark one-pixel shadow behind the tick label",
		section = LABEL_SECTION,
		position = 7
	)
	default boolean labelShadow()
	{
		return true;
	}

	@ConfigItem(
		keyName = "cycleStartAccent",
		name = "Cycle-start accent",
		description = "Use a different ring color on the first tick of each label cycle",
		section = LABEL_SECTION,
		position = 8
	)
	default boolean cycleStartAccent()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "cycleStartColor",
		name = "Cycle-start color",
		description = "Ring color used on the first tick of each cycle",
		section = LABEL_SECTION,
		position = 9
	)
	default Color cycleStartColor()
	{
		return new Color(255, 205, 70, 245);
	}
	@ConfigItem(
		keyName = "attackLastTickAccent",
		name = "Accent last attack tick",
		description = "In Attack timer mode, use a different text color when the countdown shows 1 (one tick before the estimated next attack)",
		section = LABEL_SECTION,
		position = 10
	)
	default boolean attackLastTickAccent()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
		keyName = "attackLastTickColor",
		name = "Last attack tick color",
		description = "Text color for the highlighted 1 in the attack countdown",
		section = LABEL_SECTION,
		position = 11
	)
	default Color attackLastTickColor()
	{
		return new Color(255, 205, 70, 255);
	}
	@ConfigItem(
		keyName = "showClickPulse",
		name = "Show click pulse",
		description = "Draw a short fading ring when a mouse button is pressed",
		section = CLICK_SECTION,
		position = 0
	)
	default boolean showClickPulse()
	{
		return true;
	}

	@ConfigItem(
		keyName = "clickAnchor",
		name = "Pulse anchor",
		description = "Keep each pulse where the click happened or make it follow the cursor",
		section = CLICK_SECTION,
		position = 1
	)
	default ClickAnchor clickAnchor()
	{
		return ClickAnchor.CLICK_LOCATION;
	}

	@Range(min = 50, max = 1200)
	@ConfigItem(
		keyName = "clickPulseDuration",
		name = "Pulse duration",
		description = "Duration of click feedback in milliseconds",
		section = CLICK_SECTION,
		position = 2
	)
	default int clickPulseDuration()
	{
		return 260;
	}

	@Range(min = 0, max = 50)
	@ConfigItem(
		keyName = "clickPulseExpansion",
		name = "Pulse expansion",
		description = "Pixels added to the click ring radius over its lifetime",
		section = CLICK_SECTION,
		position = 3
	)
	default int clickPulseExpansion()
	{
		return 6;
	}

	@Range(min = 1, max = 12)
	@ConfigItem(
		keyName = "clickPulseThickness",
		name = "Pulse thickness",
		description = "Width of click feedback rings in pixels",
		section = CLICK_SECTION,
		position = 4
	)
	default int clickPulseThickness()
	{
		return 2;
	}

	@Alpha
	@ConfigItem(
		keyName = "leftClickColor",
		name = "Left-click color",
		description = "Color and starting opacity of left-click pulses",
		section = CLICK_SECTION,
		position = 5
	)
	default Color leftClickColor()
	{
		return new Color(80, 190, 255, 230);
	}

	@Alpha
	@ConfigItem(
		keyName = "rightClickColor",
		name = "Right-click color",
		description = "Color and starting opacity of right-click pulses",
		section = CLICK_SECTION,
		position = 6
	)
	default Color rightClickColor()
	{
		return new Color(255, 95, 120, 230);
	}

	@Alpha
	@ConfigItem(
		keyName = "middleClickColor",
		name = "Middle-click color",
		description = "Color and starting opacity of middle or extra-button pulses",
		section = CLICK_SECTION,
		position = 7
	)
	default Color middleClickColor()
	{
		return new Color(210, 140, 255, 230);
	}

	@ConfigItem(
		keyName = "restartCycleHotkey",
		name = "Restart cycle",
		description = "Make the current tick position 1 without changing the underlying GameTick timing",
		section = CONTROLS_SECTION,
		position = 0
	)
	default Keybind restartCycleHotkey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
		keyName = "toggleOverlayHotkey",
		name = "Toggle overlay",
		description = "Show or hide the complete cursor overlay for this client session; hiding it restores the system cursor",
		section = CONTROLS_SECTION,
		position = 1
	)
	default Keybind toggleOverlayHotkey()
	{
		return Keybind.NOT_SET;
	}
}
