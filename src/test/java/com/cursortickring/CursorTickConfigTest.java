package com.cursortickring;

import java.awt.Color;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CursorTickConfigTest
{
	private final CursorTickConfig config = new CursorTickConfig()
	{
	};

	@Test
	public void usesRequestedGeneralDefaults()
	{
		assertEquals(VisibilityMode.LOGGED_IN, config.visibilityMode());
		assertFalse(config.gameViewportOnly());
		assertTrue(config.hideWhenMenuOpen());
		assertEquals(10, config.idleFadeDelaySeconds());
		assertEquals(1, config.idleFadeDurationSeconds());
		assertTrue(config.antiAliasing());
		assertTrue(config.showFeedbackOutline());
	}

	@Test
	public void usesRequestedTimingAndLabelDefaults()
	{
		assertEquals(TimingMode.FIXED, config.timingMode());
		assertEquals(600, config.tickDuration());
		assertEquals(20, config.adaptationStrength());
		assertEquals(0, config.phaseOffset());
		assertFalse(config.pulseOnTick());
		assertEquals(1, config.pulseCycleTick());
		assertEquals(110, config.pulseOnTickDuration());
		assertEquals(4, config.pulseOnTickExpansion());
		assertEquals(new Color(255, 255, 255, 210), config.pulseOnTickColor());
		assertTrue(config.showTickLabel());
		assertEquals(4, config.cycleLength());
		assertTrue(config.cycleStartAccent());
	}

	@Test
	public void usesRequestedClickAndCursorDefaults()
	{
		assertTrue(config.showClickPulse());
		assertEquals(ClickAnchor.CLICK_LOCATION, config.clickAnchor());
		assertEquals(260, config.clickPulseDuration());
		assertEquals(6, config.clickPulseExpansion());
		assertEquals(2, config.clickPulseThickness());
		assertEquals(new Color(80, 190, 255, 230), config.leftClickColor());
		assertEquals(new Color(255, 95, 120, 230), config.rightClickColor());
		assertEquals(new Color(210, 140, 255, 230), config.middleClickColor());
		assertEquals(CursorMode.SYSTEM, config.cursorMode());
		assertTrue(config.fadeOnTickReset());
		assertEquals(180, config.tickResetFadeDuration());
	}
}
