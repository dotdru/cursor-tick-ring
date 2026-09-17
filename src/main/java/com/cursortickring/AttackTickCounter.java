package com.cursortickring;

final class AttackTickCounter
{
	private int pendingPeriod;
	private volatile int remaining;
	private int lastAnimation = -1;
	private int lastFrame = -1;
	private boolean observing;

	synchronized boolean observeAnimation(int animation, int frame)
	{
		boolean restarted = observing && (animation != lastAnimation
			|| (frame >= 0 && frame < lastFrame));
		lastAnimation = animation;
		lastFrame = frame;
		observing = true;
		return animation >= 0 && restarted;
	}

	synchronized void recordAttack(int period)
	{
		if (period > 0)
		{
			pendingPeriod = period;
		}
	}

	synchronized void onGameTick()
	{

		if (pendingPeriod > 0)
		{
			remaining = pendingPeriod;
			pendingPeriod = 0;
		}
		else
		{
			remaining = Math.max(0, remaining - 1);
		}
	}

	int getRemaining()
	{
		return remaining;
	}

	synchronized void reset()
	{
		pendingPeriod = 0;
		remaining = 0;
		lastAnimation = -1;
		lastFrame = -1;
		observing = false;
	}
}
