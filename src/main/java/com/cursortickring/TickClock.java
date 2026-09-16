package com.cursortickring;

final class TickClock
{
	private static final long NANOS_PER_MILLISECOND = 1_000_000L;
	private static final double MIN_VALID_INTERVAL_FACTOR = 0.70;
	private static final double MAX_VALID_INTERVAL_FACTOR = 1.30;

	private volatile State state = State.stopped(600L * NANOS_PER_MILLISECOND);

	synchronized void reset(int expectedDurationMillis)
	{
		state = State.stopped(toNanos(expectedDurationMillis));
	}

	synchronized void onGameTick(
		long nowNanos,
		int expectedDurationMillis,
		TimingMode timingMode,
		int adaptationStrengthPercent)
	{
		State previous = state;
		long expectedNanos = toNanos(expectedDurationMillis);
		double durationNanos = expectedNanos;

		if (timingMode == TimingMode.ADAPTIVE && previous.started && nowNanos > previous.tickStartNanos)
		{
			long observedNanos = nowNanos - previous.tickStartNanos;
			double minimum = expectedNanos * MIN_VALID_INTERVAL_FACTOR;
			double maximum = expectedNanos * MAX_VALID_INTERVAL_FACTOR;

			if (observedNanos >= minimum && observedNanos <= maximum)
			{
				double strength = clamp(adaptationStrengthPercent / 100.0, 0.01, 1.0);
				durationNanos = previous.durationNanos
					+ (observedNanos - previous.durationNanos) * strength;
			}
			else
			{
				durationNanos = previous.durationNanos;
			}
		}

		state = new State(true, nowNanos, durationNanos, previous.tickNumber + 1L);
	}

	State getState()
	{
		return state;
	}

	private static long toNanos(int milliseconds)
	{
		return milliseconds * NANOS_PER_MILLISECOND;
	}

	private static double clamp(double value, double minimum, double maximum)
	{
		return Math.max(minimum, Math.min(maximum, value));
	}

	static final class State
	{
		private final boolean started;
		private final long tickStartNanos;
		private final double durationNanos;
		private final long tickNumber;

		private State(boolean started, long tickStartNanos, double durationNanos, long tickNumber)
		{
			this.started = started;
			this.tickStartNanos = tickStartNanos;
			this.durationNanos = durationNanos;
			this.tickNumber = tickNumber;
		}

		private static State stopped(long durationNanos)
		{
			return new State(false, 0L, durationNanos, 0L);
		}

		boolean isStarted()
		{
			return started;
		}

		double progressAt(long nowNanos, int phaseOffsetMillis)
		{
			if (!started)
			{
				return 0.0;
			}

			double elapsed = nowNanos - tickStartNanos
				+ phaseOffsetMillis * (double) NANOS_PER_MILLISECOND;
			return clamp(elapsed / durationNanos, 0.0, 1.0);
		}

		double ageMillisAt(long nowNanos)
		{
			if (!started)
			{
				return Double.POSITIVE_INFINITY;
			}

			return Math.max(0L, nowNanos - tickStartNanos) / (double) NANOS_PER_MILLISECOND;
		}

		double getDurationMillis()
		{
			return durationNanos / NANOS_PER_MILLISECOND;
		}

		long getTickNumber()
		{
			return tickNumber;
		}
	}
}
