package com.cursortickring;

final class ClickPulse
{
	private final long startedAtNanos;
	private final int x;
	private final int y;
	private final int button;

	ClickPulse(long startedAtNanos, int x, int y, int button)
	{
		this.startedAtNanos = startedAtNanos;
		this.x = x;
		this.y = y;
		this.button = button;
	}

	long getStartedAtNanos()
	{
		return startedAtNanos;
	}

	int getX()
	{
		return x;
	}

	int getY()
	{
		return y;
	}

	int getButton()
	{
		return button;
	}
}
