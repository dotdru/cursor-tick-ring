package com.cursortickring;

public enum RotationDirection
{
	CLOCKWISE("Clockwise", -1),
	COUNTER_CLOCKWISE("Counter-clockwise", 1);

	private final String displayName;
	private final int arcSign;

	RotationDirection(String displayName, int arcSign)
	{
		this.displayName = displayName;
		this.arcSign = arcSign;
	}

	int getArcSign()
	{
		return arcSign;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}