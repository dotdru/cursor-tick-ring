package com.cursortickring;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

final class CursorTickOverlay extends Overlay
{
	private static final double FULL_CIRCLE = 360.0;
	private static final double NANOS_PER_MILLISECOND = 1_000_000.0;
	private static final double NANOS_PER_SECOND = 1_000_000_000.0;

	private final Client client;
	private final CursorTickPlugin plugin;
	private final CursorTickConfig config;
	private final Arc2D.Double reusableArc = new Arc2D.Double(Arc2D.OPEN);

	private Font cachedLabelFont;
	private int cachedFontSize = -1;
	private boolean cachedFontBold;

	@Inject
	CursorTickOverlay(Client client, CursorTickPlugin plugin, CursorTickConfig config)
	{
		super(plugin);
		this.client = client;
		this.plugin = plugin;
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
		setPriority(PRIORITY_HIGHEST);
		setMovable(false);
		setSnappable(false);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		Point mousePosition = client.getMouseCanvasPosition();
		if (!plugin.isOverlayEnabled()
			|| mousePosition == null
			|| mousePosition.getX() < 0
			|| mousePosition.getY() < 0)
		{
			return null;
		}

		long nowNanos = System.nanoTime();
		boolean renderRing = shouldRenderRing(mousePosition);
		boolean renderCursorMarker = shouldRenderCursorMarker();
		double visibility = renderRing ? calculateIdleVisibility(nowNanos) : 0.0;
		if (visibility <= 0.0 && !renderCursorMarker)
		{
			return null;
		}

		int centerX = mousePosition.getX() + config.horizontalOffset();
		int centerY = mousePosition.getY() + config.verticalOffset();

		Graphics2D g = (Graphics2D) graphics.create();
		try
		{
			configureRendering(g);

			if (visibility > 0.0)
			{
				TickClock.State tickState = plugin.getTickState();
				double progress = tickState.progressAt(nowNanos, config.phaseOffset());
				long tickNumber = tickState.getTickNumber();
				int cyclePosition = plugin.getCyclePosition(tickNumber, config.cycleLength());

				drawClickPulses(g, mousePosition, nowNanos, visibility);
				drawTickRing(
					g,
					centerX,
					centerY,
					progress,
					cyclePosition,
					tickState,
					nowNanos,
					visibility);
				drawPulseOnTick(g, centerX, centerY, nowNanos, tickState, cyclePosition, visibility);
				drawTickLabel(
					g,
					centerX,
					centerY,
					tickNumber,
					cyclePosition,
					tickState.isStarted(),
					visibility);
			}

			if (renderCursorMarker)
			{
				drawCursorMarker(g, mousePosition.getX(), mousePosition.getY());
			}
		}
		finally
		{
			g.dispose();
		}

		return null;
	}

	private boolean shouldRenderRing(Point mousePosition)
	{
		if (config.visibilityMode() == VisibilityMode.LOGGED_IN
			&& client.getGameState() != GameState.LOGGED_IN)
		{
			return false;
		}

		if (config.hideWhenMenuOpen() && client.isMenuOpen())
		{
			return false;
		}

		if (config.gameViewportOnly())
		{
			int left = client.getViewportXOffset();
			int top = client.getViewportYOffset();
			int right = left + client.getViewportWidth();
			int bottom = top + client.getViewportHeight();
			return mousePosition.getX() >= left
				&& mousePosition.getX() < right
				&& mousePosition.getY() >= top
				&& mousePosition.getY() < bottom;
		}

		return true;
	}

	private boolean shouldRenderCursorMarker()
	{
		if (!plugin.isCustomCursorActive())
		{
			return false;
		}

		return config.cursorMode() == CursorMode.DOT
			|| config.cursorMode() == CursorMode.CROSSHAIR;
	}

	private double calculateIdleVisibility(long nowNanos)
	{
		if (config.attackTimerMode() && plugin.getAttackTicksRemaining() > 0)
		{
			return 1.0;
		}

		int delaySeconds = config.idleFadeDelaySeconds();
		if (delaySeconds <= 0)
		{
			return 1.0;
		}

		double idleSeconds = Math.max(0L, nowNanos - plugin.getLastMouseActivityNanos())
			/ NANOS_PER_SECOND;
		if (idleSeconds <= delaySeconds)
		{
			return 1.0;
		}

		int fadeSeconds = config.idleFadeDurationSeconds();
		if (fadeSeconds <= 0)
		{
			return 0.0;
		}

		return clamp(1.0 - (idleSeconds - delaySeconds) / fadeSeconds, 0.0, 1.0);
	}

	private void configureRendering(Graphics2D graphics)
	{
		Object hint = config.antiAliasing()
			? RenderingHints.VALUE_ANTIALIAS_ON
			: RenderingHints.VALUE_ANTIALIAS_OFF;
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, hint);
		graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		graphics.setRenderingHint(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			config.antiAliasing()
				? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
				: RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}

	void drawTickRing(
		Graphics2D graphics,
		int centerX,
		int centerY,
		double progress,
		int cyclePosition,
		TickClock.State tickState,
		long nowNanos,
		double visibility)
	{
		int radius = config.radius();
		int thickness = config.thickness();

		if (config.showTrack())
		{
			drawOutlinedRange(
				graphics,
				centerX,
				centerY,
				radius,
				0.0,
				FULL_CIRCLE,
				thickness,
				withAlpha(config.trackColor(), visibility));
		}

		if (!tickState.isStarted())
		{
			return;
		}

		drawTickResetFade(
			graphics,
			centerX,
			centerY,
			radius,
			thickness,
			cyclePosition,
			tickState,
			nowNanos,
			visibility);

		double activeStart;
		double activeLength;
		switch (config.ringStyle())
		{
			case REMAINING:
				activeStart = progress * FULL_CIRCLE;
				activeLength = (1.0 - progress) * FULL_CIRCLE;
				break;
			case SWEEP:
				activeLength = config.sweepSize();
				activeStart = progress * FULL_CIRCLE - activeLength / 2.0;
				break;
			case FILL:
			default:
				activeStart = 0.0;
				activeLength = progress * FULL_CIRCLE;
				break;
		}

		Color progressColor = getProgressColor(cyclePosition);
		drawOutlinedRange(
			graphics,
			centerX,
			centerY,
			radius,
			activeStart,
			activeLength,
			thickness,
			withAlpha(progressColor, visibility));

		if (config.showLeadingDot())
		{
			double markerPosition = config.ringStyle() == RingStyle.SWEEP
				? activeStart + activeLength
				: progress * FULL_CIRCLE;
			drawLeadingDot(
				graphics,
				centerX,
				centerY,
				radius,
				markerPosition,
				withAlpha(progressColor, visibility));
		}
	}

	private Color getProgressColor(int cyclePosition)
	{
		if (config.cycleStartAccent() && cyclePosition == 1)
		{
			return config.cycleStartColor();
		}

		return config.progressColor();
	}

	private void drawTickResetFade(
		Graphics2D graphics,
		int centerX,
		int centerY,
		int radius,
		float thickness,
		int cyclePosition,
		TickClock.State tickState,
		long nowNanos,
		double visibility)
	{
		if (!config.fadeOnTickReset() || config.ringStyle() != RingStyle.FILL
			|| tickState.getTickNumber() <= 1L)
		{
			return;
		}

		double durationMillis = config.tickResetFadeDuration();
		double fraction = tickState.ageMillisAt(nowNanos) / durationMillis;
		if (fraction < 0.0 || fraction >= 1.0)
		{
			return;
		}

		/* Smoothstep gives the fade a soft start and finish without a blur pass. */
		double smoothstep = fraction * fraction * (3.0 - 2.0 * fraction);
		double fade = (1.0 - smoothstep) * visibility;

		int cycleLength = Math.max(1, config.cycleLength());
		int previousCyclePosition = cyclePosition <= 1
			? cycleLength
			: cyclePosition - 1;
		Color previousColor = getProgressColor(previousCyclePosition);

		drawOutlinedRange(
			graphics,
			centerX,
			centerY,
			radius,
			0.0,
			FULL_CIRCLE,
			thickness,
			withAlpha(previousColor, fade));
	}

	private void drawOutlinedRange(
		Graphics2D graphics,
		int centerX,
		int centerY,
		int radius,
		double logicalStart,
		double logicalLength,
		float thickness,
		Color color)
	{
		if (logicalLength <= 0.0 || color.getAlpha() <= 0)
		{
			return;
		}

		if (config.showOutline())
		{
			float outlineThickness = thickness + config.outlineWidth() * 2.0f;
			graphics.setStroke(createStroke(outlineThickness));
			graphics.setColor(withAlpha(config.outlineColor(), color.getAlpha() / 255.0));
			drawLogicalRange(graphics, centerX, centerY, radius, logicalStart, logicalLength);
		}

		graphics.setStroke(createStroke(thickness));
		graphics.setColor(color);
		drawLogicalRange(graphics, centerX, centerY, radius, logicalStart, logicalLength);
	}

	private BasicStroke createStroke(float width)
	{
		return new BasicStroke(
			width,
			config.lineCap().getAwtValue(),
			BasicStroke.JOIN_ROUND);
	}

	private void drawLogicalRange(
		Graphics2D graphics,
		int centerX,
		int centerY,
		int radius,
		double logicalStart,
		double logicalLength)
	{
		double boundedLength = Math.min(FULL_CIRCLE, logicalLength);
		double logicalEnd = logicalStart + boundedLength;
		int segmentCount = Math.max(1, config.segments());
		double segmentSize = FULL_CIRCLE / segmentCount;
		double configuredGap = segmentCount == 1 ? 0.0 : config.segmentGap();
		double gap = Math.min(configuredGap, Math.max(0.0, segmentSize - 0.5));

		for (int segment = 0; segment < segmentCount; segment++)
		{
			double baseStart = segment * segmentSize + gap / 2.0;
			double baseEnd = (segment + 1) * segmentSize - gap / 2.0;

			for (int revolution = -1; revolution <= 1; revolution++)
			{
				double segmentStart = baseStart + revolution * FULL_CIRCLE;
				double segmentEnd = baseEnd + revolution * FULL_CIRCLE;
				double visibleStart = Math.max(logicalStart, segmentStart);
				double visibleEnd = Math.min(logicalEnd, segmentEnd);

				if (visibleEnd - visibleStart > 0.001)
				{
					drawArc(graphics, centerX, centerY, radius, visibleStart, visibleEnd - visibleStart);
				}
			}
		}
	}

	private void drawArc(
		Graphics2D graphics,
		int centerX,
		int centerY,
		int radius,
		double logicalStart,
		double logicalLength)
	{
		int direction = config.rotationDirection().getArcSign();
		double javaStartAngle = 90.0 - config.startAngle() + direction * logicalStart;
		double javaExtent = direction * logicalLength;
		double diameter = radius * 2.0;

		reusableArc.setArc(
			centerX - radius,
			centerY - radius,
			diameter,
			diameter,
			javaStartAngle,
			javaExtent,
			Arc2D.OPEN);
		graphics.draw(reusableArc);
	}

	private void drawLeadingDot(
		Graphics2D graphics,
		int centerX,
		int centerY,
		int radius,
		double logicalPosition,
		Color color)
	{
		double direction = config.rotationDirection() == RotationDirection.CLOCKWISE ? 1.0 : -1.0;
		double screenAngle = Math.toRadians(config.startAngle() + direction * logicalPosition);
		int dotX = (int) Math.round(centerX + Math.sin(screenAngle) * radius);
		int dotY = (int) Math.round(centerY - Math.cos(screenAngle) * radius);
		int dotSize = config.leadingDotSize();
		int dotRadius = dotSize / 2;

		if (config.showOutline())
		{
			int outlineSize = dotSize + config.outlineWidth() * 2;
			graphics.setColor(withAlpha(config.outlineColor(), color.getAlpha() / 255.0));
			graphics.fillOval(
				dotX - outlineSize / 2,
				dotY - outlineSize / 2,
				outlineSize,
				outlineSize);
		}

		graphics.setColor(color);
		graphics.fillOval(dotX - dotRadius, dotY - dotRadius, dotSize, dotSize);
	}

	private void drawPulseOnTick(
		Graphics2D graphics,
		int centerX,
		int centerY,
		long nowNanos,
		TickClock.State tickState,
		int cyclePosition,
		double visibility)
	{
		if (!config.pulseOnTick() || !tickState.isStarted())
		{
			return;
		}

		int selectedTick = config.pulseCycleTick();
		if (selectedTick > 0 && cyclePosition != selectedTick)
		{
			return;
		}

		double durationMillis = config.pulseOnTickDuration();
		double fraction = tickState.ageMillisAt(nowNanos) / durationMillis;
		if (fraction < 0.0 || fraction >= 1.0)
		{
			return;
		}

		double eased = 1.0 - Math.pow(1.0 - fraction, 3.0);
		int radius = config.radius() + (int) Math.round(config.pulseOnTickExpansion() * eased);
		double fade = Math.pow(1.0 - fraction, 2.0) * visibility;
		Color color = withAlpha(config.pulseOnTickColor(), fade);
		drawCircleWithOutline(graphics, centerX, centerY, radius, Math.max(1.0f, config.thickness() / 2.0f), color);
	}

	private void drawClickPulses(
		Graphics2D graphics,
		Point currentMouse,
		long nowNanos,
		double visibility)
	{
		double durationNanos = config.clickPulseDuration() * NANOS_PER_MILLISECOND;
		for (ClickPulse clickPulse : plugin.getClickPulses())
		{
			double ageNanos = nowNanos - clickPulse.getStartedAtNanos();
			if (!config.showClickPulse() || ageNanos >= durationNanos)
			{
				plugin.removeClickPulse(clickPulse);
				continue;
			}

			if (ageNanos < 0.0)
			{
				continue;
			}

			double fraction = clamp(ageNanos / durationNanos, 0.0, 1.0);
			double eased = 1.0 - Math.pow(1.0 - fraction, 3.0);
			int radius = config.radius() + (int) Math.round(config.clickPulseExpansion() * eased);
			int x = config.clickAnchor() == ClickAnchor.FOLLOW_CURSOR
				? currentMouse.getX()
				: clickPulse.getX();
			int y = config.clickAnchor() == ClickAnchor.FOLLOW_CURSOR
				? currentMouse.getY()
				: clickPulse.getY();
			x += config.horizontalOffset();
			y += config.verticalOffset();

			double fade = Math.pow(1.0 - fraction, 2.0) * visibility;
			Color color = withAlpha(getClickColor(clickPulse.getButton()), fade);
			drawCircleWithOutline(
				graphics,
				x,
				y,
				radius,
				config.clickPulseThickness(),
				color);
		}
	}

	private Color getClickColor(int button)
	{
		if (button == MouseEvent.BUTTON1)
		{
			return config.leftClickColor();
		}
		if (button == MouseEvent.BUTTON3)
		{
			return config.rightClickColor();
		}
		return config.middleClickColor();
	}

	private void drawCircleWithOutline(
		Graphics2D graphics,
		int centerX,
		int centerY,
		int radius,
		float thickness,
		Color color)
	{
		if (color.getAlpha() <= 0)
		{
			return;
		}

		int diameter = radius * 2;
		int x = centerX - radius;
		int y = centerY - radius;

		if (config.showFeedbackOutline())
		{
			graphics.setStroke(createStroke(thickness + config.outlineWidth() * 2.0f));
			graphics.setColor(withAlpha(config.outlineColor(), color.getAlpha() / 255.0));
			graphics.drawOval(x, y, diameter, diameter);
		}

		graphics.setStroke(createStroke(thickness));
		graphics.setColor(color);
		graphics.drawOval(x, y, diameter, diameter);
	}

	private void drawCursorMarker(Graphics2D graphics, int centerX, int centerY)
	{
		switch (config.cursorMode())
		{
			case DOT:
				drawCursorDot(graphics, centerX, centerY);
				break;
			case CROSSHAIR:
				drawCursorCrosshair(graphics, centerX, centerY);
				break;
			case HIDDEN:
			case SYSTEM:
			default:
				break;
		}
	}

	private void drawCursorDot(Graphics2D graphics, int centerX, int centerY)
	{
		int size = config.cursorMarkerSize();
		int x = centerX - size / 2;
		int y = centerY - size / 2;

		if (config.cursorMarkerOutline())
		{
			int outlineSize = size + 2;
			graphics.setColor(config.cursorMarkerOutlineColor());
			graphics.fillOval(
				centerX - outlineSize / 2,
				centerY - outlineSize / 2,
				outlineSize,
				outlineSize);
		}

		graphics.setColor(config.cursorMarkerColor());
		graphics.fillOval(x, y, size, size);
	}

	private void drawCursorCrosshair(Graphics2D graphics, int centerX, int centerY)
	{
		int halfSize = Math.max(1, config.cursorMarkerSize() / 2);
		int gap = Math.min(config.cursorMarkerGap(), Math.max(0, halfSize - 1));
		float thickness = config.cursorMarkerThickness();

		if (config.cursorMarkerOutline())
		{
			graphics.setStroke(new BasicStroke(
				thickness + 2.0f,
				BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));
			graphics.setColor(config.cursorMarkerOutlineColor());
			drawCrosshairArms(graphics, centerX, centerY, halfSize, gap);
		}

		graphics.setStroke(new BasicStroke(
			thickness,
			BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND));
		graphics.setColor(config.cursorMarkerColor());
		drawCrosshairArms(graphics, centerX, centerY, halfSize, gap);
	}

	private static void drawCrosshairArms(
		Graphics2D graphics,
		int centerX,
		int centerY,
		int halfSize,
		int gap)
	{
		graphics.drawLine(centerX - halfSize, centerY, centerX - gap, centerY);
		graphics.drawLine(centerX + gap, centerY, centerX + halfSize, centerY);
		graphics.drawLine(centerX, centerY - halfSize, centerX, centerY - gap);
		graphics.drawLine(centerX, centerY + gap, centerX, centerY + halfSize);
	}

	private void drawTickLabel(
		Graphics2D graphics,
		int centerX,
		int centerY,
		long tickNumber,
		int cyclePosition,
		boolean started,
		double visibility)
	{
		if (!started)
		{
			return;
		}

		String label = getTickLabel(tickNumber, cyclePosition);
		if (label == null)
		{
			return;
		}
		Font font = getLabelFont();
		graphics.setFont(font);
		FontMetrics metrics = graphics.getFontMetrics(font);
		int x = centerX - metrics.stringWidth(label) / 2;
		int y;
		switch (config.tickLabelPosition())
		{
			case ABOVE:
				y = centerY - config.radius() - config.thickness() / 2 - 4;
				break;
			case CENTER:
				y = centerY + (metrics.getAscent() - metrics.getDescent()) / 2;
				break;
			case BELOW:
			default:
				y = centerY + config.radius() + config.thickness() / 2 + metrics.getAscent() + 4;
				break;
		}

		Color textColor = config.labelColor();

		if (config.attackTimerMode()
			&& config.attackLastTickAccent()
			&& "1".equals(label))
		{
			textColor = config.attackLastTickColor();
		}

		Color labelColor = withAlpha(textColor, visibility);
		
		if (config.labelShadow())
		{
			graphics.setColor(new Color(0, 0, 0, labelColor.getAlpha()));
			graphics.drawString(label, x + 1, y + 1);
		}

		graphics.setColor(labelColor);
		graphics.drawString(label, x, y);
	}

	String getTickLabel(long tickNumber, int cyclePosition)
	{
		if (config.attackTimerMode())
		{
			int remaining = plugin.getAttackTicksRemaining();
			return remaining > 0 ? Integer.toString(remaining) : null;
		}
		if (!config.showTickLabel())
		{
			return null;
		}

		int cycleLength = Math.max(1, config.cycleLength());
		switch (config.tickLabelMode())
		{
			case CYCLE_REMAINING:
				return Integer.toString(cycleLength - cyclePosition + 1);
			case TOTAL:
				return Long.toString(tickNumber);
			case CYCLE_POSITION:
			default:
				return Integer.toString(cyclePosition);
		}
	}

	private Font getLabelFont()
	{
		int size = config.labelFontSize();
		boolean bold = config.boldLabel();
		if (cachedLabelFont == null || cachedFontSize != size || cachedFontBold != bold)
		{
			cachedFontSize = size;
			cachedFontBold = bold;
			cachedLabelFont = new Font(Font.SANS_SERIF, bold ? Font.BOLD : Font.PLAIN, size);
		}
		return cachedLabelFont;
	}

	private static Color withAlpha(Color color, double multiplier)
	{
		double boundedMultiplier = clamp(multiplier, 0.0, 1.0);
		if (boundedMultiplier >= 0.9999)
		{
			return color;
		}
		int alpha = (int) Math.round(color.getAlpha() * boundedMultiplier);
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}

	private static double clamp(double value, double minimum, double maximum)
	{
		return Math.max(minimum, Math.min(maximum, value));
	}
}
