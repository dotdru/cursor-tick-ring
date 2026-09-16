package com.cursortickring;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TickCycleTest
{
	@Test
	public void startsAtOneAndRepeats()
	{
		TickCycle cycle = new TickCycle();

		assertEquals(0, cycle.positionAt(0L, 4));
		assertEquals(1, cycle.positionAt(1L, 4));
		assertEquals(4, cycle.positionAt(4L, 4));
		assertEquals(1, cycle.positionAt(5L, 4));
	}

	@Test
	public void restartMakesTheCurrentTickPositionOne()
	{
		TickCycle cycle = new TickCycle();
		cycle.restartAt(11L);

		assertEquals(1, cycle.positionAt(11L, 4));
		assertEquals(2, cycle.positionAt(12L, 4));
		assertEquals(1, cycle.positionAt(15L, 4));
	}

	@Test
	public void resetRestoresTheOriginalCycleOrigin()
	{
		TickCycle cycle = new TickCycle();
		cycle.restartAt(7L);
		cycle.reset();

		assertEquals(3, cycle.positionAt(7L, 4));
	}
}
