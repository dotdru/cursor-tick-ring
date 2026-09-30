package com.cursortickring;

import net.runelite.api.gameval.AnimationID;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AttackTickCounterTest
{
	@Test
	public void addsComboFoodDelayToTheCurrentCooldown()
	{
		AttackTickCounter counter = new AttackTickCounter();
		counter.recordAttack(4);
		counter.onGameTick();
		counter.recordFood(3);
		counter.recordFood(2);
		counter.onGameTick();
		assertEquals(8, counter.getRemaining());
		counter.onGameTick();
		assertEquals(7, counter.getRemaining());
	}

	@Test
	public void foodDoesNotStartACooldownWhenReadyToAttack()
	{
		AttackTickCounter counter = new AttackTickCounter();
		counter.recordFood(3);
		counter.onGameTick();
		assertEquals(0, counter.getRemaining());
		counter.recordAttack(1);
		counter.onGameTick();
		counter.recordFood(3);
		counter.onGameTick();
		assertEquals(0, counter.getRemaining());
	}

	@Test
	public void anObservedAttackReplacesEarlierFoodDelay()
	{
		AttackTickCounter counter = new AttackTickCounter();
		counter.recordFood(3);
		counter.recordAttack(5);
		counter.recordFood(2);
		counter.onGameTick();
		assertEquals(7, counter.getRemaining());
		counter.reset();
		counter.onGameTick();
		assertEquals(0, counter.getRemaining());
	}

	@Test
	public void detectsInitialAndRepeatedAncientCastsWithoutCountingEveryFrame()
	{
		AttackTickCounter counter = new AttackTickCounter();
		int animation = AnimationID.ZAROS_VERTICAL_CASTING_WALKMERGE;
		assertTrue(counter.observeAnimation(animation, 0));
		assertFalse(counter.observeAnimation(animation, 0));
		assertFalse(counter.observeAnimation(animation, 8));
		assertTrue(counter.observeAnimation(animation, 0));
		assertEquals(5, AttackAnimations.classify(animation).period(6, false));
		assertEquals(5, AttackAnimations.classify(AnimationID.ZAROS_CASTING_WALKMERGE).period(4, true));
		assertEquals(5, AttackAnimations.classify(AnimationID.ZAROS_CASTING).period(0, false));
	}
}
