package com.cursortickring;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

final class CursorTickOverlay extends Overlay
{
	private final Client client;
	private final CursorTickConfig config;

	@Inject
	CursorTickOverlay(Client client, CursorTickPlugin plugin, CursorTickConfig config)
	{
		super(plugin);
		this.client = client;
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

		int r = config.radius();
		double d = r * 2.0;
		Ellipse2D.Double circle = new Ellipse2D.Double(
			mouse.getX() - r,
			mouse.getY() - r,
			d,
			d);

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
			g.setColor(config.progressColor());
			g.draw(circle);
		}
		finally
		{
			g.dispose();
		}

		return null;
	}
}