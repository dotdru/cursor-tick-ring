package com.cursortickring;

public enum VisibilityMode
{
	LOGGED_IN("Logged in only"),
	ALWAYS("Always");

	private final String displayName;

	VisibilityMode(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
