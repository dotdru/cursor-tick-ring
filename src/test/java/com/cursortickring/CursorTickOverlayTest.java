package com.cursortickring;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CursorTickOverlayTest
{
	@Test
	public void remainingRingFadesInOnResetInBothDirections()
	{
		for (RotationDirection direction : RotationDirection.values())
		{
			assertEquals(0, opacity(RingStyle.REMAINING, direction, true, 0));
			int middle = opacity(RingStyle.REMAINING, direction, true, 90);
			int end = opacity(RingStyle.REMAINING, direction, true, 180);
			assertTrue(middle > 0);
			assertTrue(end > middle);
			assertEquals(opacity(RingStyle.REMAINING, direction, false, 180), end);
		}
	}

	@Test
	public void keepsFillFadeAndLeavesRotatingSweepUnaffected()
	{
		assertTrue(opacity(RingStyle.FILL, RotationDirection.CLOCKWISE, true, 0) > 0);
		assertEquals(0, opacity(RingStyle.FILL, RotationDirection.CLOCKWISE, false, 0));
		assertEquals(opacity(RingStyle.SWEEP, RotationDirection.CLOCKWISE, false, 0),
			opacity(RingStyle.SWEEP, RotationDirection.CLOCKWISE, true, 0));
		assertTrue(opacity(RingStyle.REMAINING, RotationDirection.CLOCKWISE, false, 0) > 0);
	}

	private int opacity(RingStyle style, RotationDirection direction, boolean fade, int ageMillis)
	{
		CursorTickConfig config = new CursorTickConfig()
		{
			@Override
			public RingStyle ringStyle()
			{
				return style;
			}

			@Override
			public RotationDirection rotationDirection()
			{
				return direction;
			}

			@Override
			public boolean fadeOnTickReset()
			{
				return fade;
			}

			@Override
			public boolean showTrack()
			{
				return false;
			}

			@Override
			public boolean showOutline()
			{
				return false;
			}

			@Override
			public Color progressColor()
			{
				return Color.WHITE;
			}

			@Override
			public Color cycleStartColor()
			{
				return Color.WHITE;
			}
		};
		TickClock clock = new TickClock();
		clock.onGameTick(0, 600, TimingMode.FIXED, 0);
		clock.onGameTick(600_000_000L, 600, TimingMode.FIXED, 0);
		long now = 600_000_000L + ageMillis * 1_000_000L;
		BufferedImage image = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		new CursorTickOverlay(null, new CursorTickPlugin(), config).drawTickRing(graphics,
			40, 40, clock.getState().progressAt(now, 0), 2, clock.getState(), now, 1.0);
		graphics.dispose();
		int opacity = 0;
		for (int y = 0; y < image.getHeight(); y++)
		{
			for (int x = 0; x < image.getWidth(); x++)
			{
				opacity += image.getRGB(x, y) >>> 24;
			}
		}
		return opacity;
	}
}
