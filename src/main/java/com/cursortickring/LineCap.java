package com.cursortickring;

import java.awt.BasicStroke;

public enum LineCap
{
	ROUND("Round", BasicStroke.CAP_ROUND),
	SQUARE("Square", BasicStroke.CAP_SQUARE),
	FLAT("Flat", BasicStroke.CAP_BUTT);

	private final String displayName;
	private final int awtValue;

	LineCap(String displayName, int awtValue)
	{
		this.displayName = displayName;
		this.awtValue = awtValue;
	}

	int getAwtValue()
	{
		return awtValue;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
