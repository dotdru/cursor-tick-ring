package com.cursortickring;

final class TickClock
{
	private static final long NANOS_PER_MILLISECOND = 1_000_000L;
	private static final long TICK_NANOS = 600L * NANOS_PER_MILLISECOND;

	private volatile State state = State.stopped();

	synchronized void onGameTick(long nowNanos)
	{
		State previous = state;
		state = new State(true, nowNanos, previous.tickNumber + 1L);
	}

	void reset()
	{
		state = State.stopped();
	}

	State getState()
	{
		return state;
	}

	static final class State
	{
		private final boolean started;
		private final long tickStartNanos;
		private final long tickNumber;

		private State(boolean started, long tickStartNanos, long tickNumber)
		{
			this.started = started;
			this.tickStartNanos = tickStartNanos;
			this.tickNumber = tickNumber;
		}

		private static State stopped()
		{
			return new State(false, 0L, 0L);
		}

		boolean isStarted()
		{
			return started;
		}

		double progressAt(long nowNanos)
		{
			if (!started)
			{
				return 0.0;
			}

			double elapsed = nowNanos - tickStartNanos;
			return clamp(elapsed / TICK_NANOS, 0.0, 1.0);
		}

		long getTickNumber()
		{
			return tickNumber;
		}
	}

	private static double clamp(double value, double minimum, double maximum)
	{
		return Math.max(minimum, Math.min(maximum, value));
	}
}