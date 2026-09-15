package com.cursortickring;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TickClockTest
{
	private static final long MILLISECOND = 1_000_000L;

	@Test
	public void startsOnlyWhenAGameTickArrives()
	{
		TickClock clock = new TickClock();
		assertFalse(clock.getState().isStarted());

		long tick = 1_000L * MILLISECOND;
		clock.onGameTick(tick);

		assertTrue(clock.getState().isStarted());
		assertEquals(1L, clock.getState().getTickNumber());
	}

	@Test
	public void fillsAndClampsAtOneTick()
	{
		TickClock clock = new TickClock();
		long tick = 1_000L * MILLISECOND;
		clock.onGameTick(tick);

		TickClock.State state = clock.getState();
		assertEquals(0.0, state.progressAt(tick), 0.0001);
		assertEquals(0.5, state.progressAt(tick + 300L * MILLISECOND), 0.0001);
		assertEquals(1.0, state.progressAt(tick + 900L * MILLISECOND), 0.0001);
	}

	@Test
	public void eachGameTickResetsAndIncrements()
	{
		TickClock clock = new TickClock();
		long first = 2_000L * MILLISECOND;
		clock.onGameTick(first);
		clock.onGameTick(first + 620L * MILLISECOND);

		assertEquals(2L, clock.getState().getTickNumber());
		assertEquals(0.25,
			clock.getState().progressAt(first + 770L * MILLISECOND),
			0.0001);
	}
}