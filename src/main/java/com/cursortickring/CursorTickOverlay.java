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

		Graphics2D g = (Graphics2D) graphics.create();
		try
		{
			g.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
			g.setStroke(new BasicStroke(
				config.thickness(),
				BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));

			if (config.showTrack())
			{
				drawArc(g, mouse, config.radius(), 90.0, -FULL_CIRCLE, config.trackColor());
			}

			if (tickState.isStarted())
			{
				drawArc(
					g,
					mouse,
					config.radius(),
					90.0,
					-FULL_CIRCLE * progress,
					config.progressColor());
			}
		}
		finally
		{
			g.dispose();
		}

		return null;
	}

	private static void drawArc(
		Graphics2D graphics,
		Point center,
		int radius,
		double startAngle,
		double extent,
		Color color)
	{
		double diameter = radius * 2.0;
		Arc2D.Double arc = new Arc2D.Double(
			center.getX() - radius,
			center.getY() - radius,
			diameter,
			diameter,
			startAngle,
			extent,
			Arc2D.OPEN);
		graphics.setColor(color);
		graphics.draw(arc);
	}
}