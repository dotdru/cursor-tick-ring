package com.cursortickring;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

	private final Client client;
	private final CursorTickPlugin plugin;
	private final CursorTickConfig config;

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
        Point mouse = client.getMouseCanvasPosition();

        if (mouse == null
            || mouse.getX() < 0
            || mouse.getY() < 0
            || client.getGameState() != GameState.LOGGED_IN)
        {
            return null;
        }

        long nowNanos = System.nanoTime();
        TickClock.State tickState = plugin.getTickState();
        double progress = tickState.progressAt(nowNanos);

        int centerX = mouse.getX();
        int centerY = mouse.getY();

        Graphics2D g = (Graphics2D) graphics.create();
        try
        {
            g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

            g.setRenderingHint(
                RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);

            g.setStroke(new BasicStroke(
                config.thickness(),
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

            drawTickRing(
                g,
                centerX,
                centerY,
                progress,
                tickState.isStarted());
        }
        finally
        {
            g.dispose();
        }

	return null;
}

    private void drawTickRing(
        Graphics2D graphics,
        int centerX,
        int centerY,
        double progress,
        boolean started)
    {
        if (config.showTrack())
        {
            drawLogicalRange(
                graphics,
                centerX,
                centerY,
                config.radius(),
                0.0,
                FULL_CIRCLE,
                config.trackColor());
        }

        if (!started)
        {
            return;
        }

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

        drawLogicalRange(
            graphics,
            centerX,
            centerY,
            config.radius(),
            activeStart,
            activeLength,
            config.progressColor());
    }

    private void drawLogicalRange(
        Graphics2D graphics,
        int centerX,
        int centerY,
        int radius,
        double logicalStart,
        double logicalLength,
        Color color)
    {
        drawArc(graphics, centerX, centerY, radius, logicalStart, logicalLength, color);
    }

    private void drawArc(
        Graphics2D graphics,
        int centerX,
        int centerY,
        int radius,
        double logicalStart,
        double logicalLength,
        Color color)
    {
        int direction = config.rotationDirection().getArcSign();
        double javaStartAngle = 90.0 - config.startAngle() + direction * logicalStart;
        double javaExtent = direction * logicalLength;
        double diameter = radius * 2.0;

        Arc2D.Double arc = new Arc2D.Double(
            centerX - radius,
            centerY - radius,
            diameter,
            diameter,
            javaStartAngle,
            javaExtent,
            Arc2D.OPEN);
        graphics.setColor(color);
        graphics.draw(arc);
    }
}