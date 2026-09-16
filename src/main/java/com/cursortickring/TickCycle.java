package com.cursortickring;

final class TickCycle
{
	private volatile long firstTickNumber = 1L;

	void reset()
	{
		firstTickNumber = 1L;
	}

	void restartAt(long currentTickNumber)
	{
		firstTickNumber = Math.max(1L, currentTickNumber);
	}

	int positionAt(long tickNumber, int cycleLength)
	{
		if (tickNumber <= 0L)
		{
			return 0;
		}

		int length = Math.max(1, cycleLength);
		long start = Math.min(firstTickNumber, tickNumber);
		return Math.floorMod(tickNumber - start, length) + 1;
	}
}
