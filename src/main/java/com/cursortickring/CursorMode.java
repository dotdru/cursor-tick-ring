package com.cursortickring;

public enum CursorMode
{
	SYSTEM("System cursor", false),
	DOT("Dot", true),
	CROSSHAIR("Crosshair", true),
	HIDDEN("Hidden", true);

	private final String displayName;
	private final boolean hidesSystemCursor;

	CursorMode(String displayName, boolean hidesSystemCursor)
	{
		this.displayName = displayName;
		this.hidesSystemCursor = hidesSystemCursor;
	}

	boolean hidesSystemCursor()
	{
		return hidesSystemCursor;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
