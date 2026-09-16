package com.cursortickring;

public enum TickLabelMode
{
	CYCLE_POSITION("Position in cycle"),
	CYCLE_REMAINING("Ticks remaining"),
	TOTAL("Total tick count");

	private final String displayName;

	TickLabelMode(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
