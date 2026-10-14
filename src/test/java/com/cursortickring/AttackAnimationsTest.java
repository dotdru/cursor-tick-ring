package com.cursortickring;

import net.runelite.api.gameval.AnimationID;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AttackAnimationsTest
{
	@Test
	public void crossbowVariantsUseTheEquippedWeaponsSpeedAndAttackStyle()
	{
		int[] animations = {
			AnimationID.XBOWS_HUMAN_FIRE_AND_RELOAD,
			AnimationID.XBOWS_HUMAN_FIRE_AND_RELOAD_PVN,
			AnimationID.XBOWS_HUMAN_FIRE_AND_RELOAD_NO_STALL,
			AnimationID.ZCB_ATTACK,
			AnimationID.ZCB_ATTACK_PVN,
			AnimationID.HUMAN_XBOWS_LEAGUE03_ATTACK,
			AnimationID.HUMAN_XBOWS_LEAGUE03_ATTACK_PVN
		};
		for (int animation : animations)
		{
			AttackAnimations.Kind kind = AttackAnimations.classify(animation);
			// Armadyl crossbow: 6 ticks on accurate/longrange, 5 on rapid.
			assertEquals(6, kind.period(6, false));
			assertEquals(5, kind.period(6, true));
			// A cosmetic crossbow animation must not set another weapon's speed to 6.
			assertEquals(4, kind.period(4, false));
			assertEquals(3, kind.period(4, true));
			assertEquals(0, kind.period(0, true));
		}
	}

	@Test
	public void repeatedZaryteReplacementShotsRestartTheArmadylCountdownOnce()
	{
		for (int animation : new int[]{AnimationID.ZCB_ATTACK, AnimationID.ZCB_ATTACK_PVN})
		{
			AttackTickCounter counter = new AttackTickCounter();
			int period = AttackAnimations.classify(animation).period(6, true);
			assertTrue(counter.observeAnimation(animation, 0));
			counter.recordAttack(period);
			counter.onGameTick();
			for (int tick = 1; tick <= 5; tick++)
			{
				assertEquals(6 - tick, counter.getRemaining());
				assertEquals(tick, counter.getCyclePosition());
				assertFalse(counter.observeAnimation(animation, tick));
				assertFalse(counter.observeAnimation(animation, tick));
				if (tick < 5)
				{
					counter.onGameTick();
				}
			}
			assertTrue(counter.observeAnimation(animation, 0));
			counter.recordAttack(period);
			counter.onGameTick();
			assertEquals(5, counter.getRemaining());
			assertEquals(1, counter.getCyclePosition());
		}
	}

	@Test
	public void throwingKnivesUseRapidTimingIncludingPoisonedDragonKnives()
	{
		for (int animation : new int[]{AnimationID.HUMAN_STAKE2, AnimationID.HUMAN_STAKE2_PVN,
			AnimationID.HUMAN_DRAGON_KNIFE, AnimationID.HUMAN_DRAGON_KNIFE_P})
		{
			assertEquals(3, AttackAnimations.classify(animation).period(3, false));
			assertEquals(2, AttackAnimations.classify(animation).period(3, true));
		}
	}

	@Test
	public void distinctMeleeAnimationsRetainTheirWeaponTiming()
	{
		assertPeriod(AnimationID.HUMAN_DINHS_BULWARK_BASH, 7);
		assertPeriod(AnimationID.DRAGON_PICKAXE_ANIM, 7);
		assertPeriod(AnimationID.ANCIENT_AXE_SLASH, 5);
		assertPeriod(AnimationID.ANCIENT_AXE_CRUSH, 5);
		assertPeriod(AnimationID.GODWARS_GODSWORD_ZAMORAK_PLAYER, 5);
		assertPeriod(AnimationID.DH_SWORD_UPDATE_BLOCK, 6);
		assertPeriod(AnimationID.FORESTRY_2H_AXE_ATTACK, 5);
	}

	@Test
	public void poweredMagicAnimationsUseTheirOwnSpeedRatherThanTheSpellDefault()
	{
		assertPeriod(AnimationID.POG_WARPED_SCEPTRE_ATTACK, 4);
		assertPeriod(AnimationID.TOA_SOT_CAST_B, 5);
		assertPeriod(AnimationID.HUMAN_SPECIAL_ACCURSED, 4);
		assertPeriod(AnimationID.NIGHTMARE_STAFF_SPECIAL, 5);
		assertEquals(0, AttackAnimations.classify(AnimationID.POG_WARPED_SCEPTRE_ATTACK).period(0, false));
	}

	@Test
	public void standardSpecialAttacksRetainTheirWeaponCooldown()
	{
		assertPeriod(AnimationID.PUNCTURE, 4);
		assertPeriod(AnimationID.ABYSSAL_DAGGER_SPECIAL, 4);
		assertPeriod(AnimationID.HUMAN_DRAGON_CLAWS_SPEC, 4);
		assertPeriod(AnimationID.AGS_SPECIAL_PLAYER, 6);
		assertPeriod(AnimationID.AGS_SPECIAL_ORNATE_PLAYER, 6);
		assertPeriod(AnimationID.BGS_SPECIAL_PLAYER, 6);
		assertPeriod(AnimationID.HUMAN_ELDER_MAUL_SPEC, 6);
		assertPeriod(AnimationID.DRAGON_WARHAMMER_SA_PLAYER, 6);
		assertPeriod(AnimationID.OLAF2_BRINE_SABRE_SPECIAL, 5);
		assertPeriod(AnimationID.ANCIENT_AXE_SPECIAL, 5);
		assertPeriod(AnimationID.HUMAN_SPECIAL_VOIDWAKER, 4);
	}

	@Test
	public void nonAttacksAndInstantSpecialsDoNotStartANormalWeaponCooldown()
	{
		int[] animations = {
			-1,
			AnimationID.XBOWS_HUMAN_READY,
			AnimationID.XBOWS_HUMAN_RUN,
			AnimationID.XBOWS_HUMAN_FIRE_AND_CLIMB_GRAPPLE,
			AnimationID.HUMAN_EAT,
			AnimationID.HUMAN_DINHS_BULWARK_BLOCK,
			AnimationID.DH_SWORD_UPDATE_DEFEND,
			AnimationID.HUMAN_CASTHIGHLVLALCHEMY,
			AnimationID.HUMAN_SPELLCAST_RESURRECT,
			AnimationID.SLAYER_GRANITE_MAUL_SPECIAL_ATTACK
		};
		for (int animation : animations)
		{
			assertEquals(AttackAnimations.Kind.NONE, AttackAnimations.classify(animation));
			assertEquals(0, AttackAnimations.classify(animation).period(6, true));
		}
	}

	private static void assertPeriod(int animation, int weaponSpeed)
	{
		assertEquals(weaponSpeed, AttackAnimations.classify(animation).period(weaponSpeed, false));
		// The second melee/magic combat style is not the ranged rapid style.
		assertEquals(weaponSpeed, AttackAnimations.classify(animation).period(weaponSpeed, true));
	}
}
