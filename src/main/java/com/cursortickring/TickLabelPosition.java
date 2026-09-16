package com.cursortickring;

public enum TickLabelPosition
{
	CENTER("Center"),
	ABOVE("Above ring"),
	BELOW("Below ring");

	private final String displayName;

	TickLabelPosition(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
