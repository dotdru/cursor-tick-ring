package com.cursortickring;

final class AttackTickCounter
{
	private int pendingPeriod;
	private int pendingFoodDelay;
	private volatile int remaining;
	private int ticksSinceAttack;
	private volatile int previousCyclePosition;
	private int lastAnimation = -1;
	private int lastFrame = -1;
	private boolean observing;

	synchronized boolean observeAnimation(int animation, int frame)
	{
		boolean restarted = !observing || animation != lastAnimation
			|| (frame >= 0 && frame < lastFrame);
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
			pendingFoodDelay = 0;
		}
	}

	synchronized void recordFood(int delay)
	{
		pendingFoodDelay += Math.max(0, delay);
	}

	synchronized void onGameTick()
	{
		previousCyclePosition = getCyclePosition();
		if (pendingPeriod > 0)
		{
			remaining = pendingPeriod;
			ticksSinceAttack = 0;
			pendingPeriod = 0;
		}
		else
		{
			remaining = Math.max(0, remaining - 1);
			if (remaining > 0)
			{
				ticksSinceAttack++;
			}
		}
		if (remaining > 0)
		{
			remaining += pendingFoodDelay;
		}
		pendingFoodDelay = 0;
	}

	int getRemaining()
	{
		return remaining;
	}

	synchronized int getCyclePosition()
	{
		return remaining > 0 ? ticksSinceAttack + 1 : 0;
	}

	int getPreviousCyclePosition()
	{
		return previousCyclePosition;
	}

	synchronized void reset()
	{
		pendingPeriod = 0;
		pendingFoodDelay = 0;
		remaining = 0;
		ticksSinceAttack = 0;
		previousCyclePosition = 0;
		lastAnimation = -1;
		lastFrame = -1;
		observing = false;
	}
}
