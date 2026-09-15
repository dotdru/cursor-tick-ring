package com.cursortickring;

public enum RingStyle
{
	FILL("Fill / progress"),
	REMAINING("Cooldown / remaining"),
	SWEEP("Rotating sweep");

	private final String displayName;

	RingStyle(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}