package com.cursortickring;

public enum TimingMode
{
	FIXED("Fixed duration"),
	ADAPTIVE("Adaptive smoothing");

	private final String displayName;

	TimingMode(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
