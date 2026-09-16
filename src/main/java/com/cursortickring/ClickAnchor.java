package com.cursortickring;

public enum ClickAnchor
{
	CLICK_LOCATION("Click location"),
	FOLLOW_CURSOR("Follow cursor");

	private final String displayName;

	ClickAnchor(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
