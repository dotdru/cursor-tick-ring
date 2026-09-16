package com.cursortickring;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TickClockTest
{
	private static final long MILLISECOND = 1_000_000L;

	@Test
	public void fixedClockIsResetByGameTicks()
	{
		TickClock clock = new TickClock();
		clock.reset(600);
		assertFalse(clock.getState().isStarted());

		long firstTick = 1_000L * MILLISECOND;
		clock.onGameTick(firstTick, 600, TimingMode.FIXED, 20);

		TickClock.State first = clock.getState();
		assertTrue(first.isStarted());
		assertEquals(1L, first.getTickNumber());
		assertEquals(0.0, first.progressAt(firstTick, 0), 0.0001);
		assertEquals(0.5, first.progressAt(firstTick + 300L * MILLISECOND, 0), 0.0001);
		assertEquals(1.0, first.progressAt(firstTick + 900L * MILLISECOND, 0), 0.0001);

		long secondTick = firstTick + 700L * MILLISECOND;
		clock.onGameTick(secondTick, 600, TimingMode.FIXED, 20);
		TickClock.State second = clock.getState();
		assertEquals(2L, second.getTickNumber());
		assertEquals(600.0, second.getDurationMillis(), 0.0001);
		assertEquals(0.25, second.progressAt(secondTick + 150L * MILLISECOND, 0), 0.0001);
	}

	@Test
	public void phaseOffsetRunsAheadOrBehindWithoutWrapping()
	{
		TickClock clock = new TickClock();
		long tick = 2_000L * MILLISECOND;
		clock.onGameTick(tick, 600, TimingMode.FIXED, 20);
		TickClock.State state = clock.getState();

		assertEquals(2.0 / 3.0, state.progressAt(tick + 300L * MILLISECOND, 100), 0.0001);
		assertEquals(1.0 / 3.0, state.progressAt(tick + 300L * MILLISECOND, -100), 0.0001);
		assertEquals(0.0, state.progressAt(tick, -200), 0.0001);
		assertEquals(1.0, state.progressAt(tick + 550L * MILLISECOND, 100), 0.0001);
	}

	@Test
	public void adaptiveClockSmoothsValidIntervalsAndRejectsOutliers()
	{
		TickClock clock = new TickClock();
		long firstTick = 5_000L * MILLISECOND;
		clock.onGameTick(firstTick, 600, TimingMode.ADAPTIVE, 50);
		clock.onGameTick(firstTick + 660L * MILLISECOND, 600, TimingMode.ADAPTIVE, 50);

		TickClock.State smoothed = clock.getState();
		assertEquals(630.0, smoothed.getDurationMillis(), 0.0001);

		clock.onGameTick(firstTick + 1_660L * MILLISECOND, 600, TimingMode.ADAPTIVE, 50);
		assertEquals(630.0, clock.getState().getDurationMillis(), 0.0001);
	}

	@Test
	public void resetClearsSynchronizationAndTickCount()
	{
		TickClock clock = new TickClock();
		clock.onGameTick(1_000L, 600, TimingMode.FIXED, 20);
		clock.reset(650);

		TickClock.State reset = clock.getState();
		assertFalse(reset.isStarted());
		assertEquals(0L, reset.getTickNumber());
		assertEquals(650.0, reset.getDurationMillis(), 0.0001);
	}
}
