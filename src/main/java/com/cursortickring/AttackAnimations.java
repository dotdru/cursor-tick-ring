package com.cursortickring;

import net.runelite.api.gameval.AnimationID;

final class AttackAnimations
{
	private AttackAnimations()
	{
	}

	static Kind classify(int animation)
	{
		switch (animation)
		{
			case AnimationID.HUMAN_EYE_OF_AYAK_NORMAL:
			case AnimationID.HUMAN_ATTACK_SALAMANDER:
				return Kind.WEAPON_RATE;

			case AnimationID.HUMAN_HALLOWFELL_CHOP:
			case AnimationID.HUMAN_HALLOWFELL_SMASH:
			case AnimationID.HUMAN_HALLOWFELL_SLASH:
			case AnimationID.HUMAN_WEAPONS_HALLOWED_FLAIL01_ATTACK01:
			case AnimationID.HUMAN_WEAPONS_HALLOWED_FLAIL01_ATTACK02:
			case AnimationID.HUMAN_WEAPONS_CRIMSON_KISTEN_ATTACK:
			case AnimationID.HUMAN_WEAPONS_CRIMSON_KISTEN_ATTACK_ALT:
			case AnimationID.HUMAN_HALBERD_VIRULENCE_01:
			case AnimationID.HUMAN_HALBERD_VIRULENCE_02:
			case AnimationID.HUMAN_HALBERD_VIRULENCE_03:
			case AnimationID.HUMAN_DHUNTER_LANCE_ATTACK:
			case AnimationID.HUMAN_DHUNTER_LANCE_SLASH:
			case AnimationID.HUMAN_DHUNTER_LANCE_CRUSH:
			case AnimationID.DH_SWORD_UPDATE_CHOP:
			case AnimationID.DH_SWORD_UPDATE_SLASH:
			case AnimationID.DH_SWORD_UPDATE_SMASH:
			case AnimationID.ABYSSAL_DAGGER_HACK:
			case AnimationID.ABYSSAL_DAGGER_LUNGE:
			case AnimationID.ABYSSAL_BLUDGEON_CRUSH:
			case AnimationID.SLAYER_GRANITE_MAUL_ATTACK:
			case AnimationID.BRAIN_PLAYER_ANCHOR_ATTACK:
			case AnimationID.HUMAN_INQUISITORS_MACE_CRUSH:
			case AnimationID.HUMAN_NIGHTMARE_STAFF_CRUSH:
			case AnimationID.WILD_CAVE_CHAINMACE_CRUSH:
			case AnimationID.WILD_CAVE_CHAINMACE_STAB:
			case AnimationID.BARROWS_QUARTERSTAFF_ATTACK:
			case AnimationID.BARROWS_WAR_SPEAR_STAB:
			case AnimationID.BARROWS_WAR_SPEAR_SLASH:
			case AnimationID.BARROWS_WAR_SPEAR_CRUSH:
			case AnimationID.HUMAN_DINHS_BULWARK_CRUSH:
			case AnimationID.HUMAN_2H_AXE_ATTACK:
			case AnimationID.PMOON_MACUAHUITL_CRUSH:
			case AnimationID.SULPHUR_BLADE_SLASH:
			case AnimationID.HUMAN_SPELLSPEAR_STAB:
			case AnimationID.TECPATL_STAB:
			case AnimationID.HUMAN_INFERNAL_TECPATL_ATTACK:
			case AnimationID.HUMAN_KARAMBIT_ATTACK:
			case AnimationID.IVANDIS_FLAIL_ATTACK:
			case AnimationID.BATTLEAXE_CRUSH:
			case AnimationID.DTTD_PLAYER_STAB_BONE_DAGGER:
			case AnimationID.STAB_WOLBANEDAGGER:
			case AnimationID.HUMAN_KNIFE_SLASH:
			case AnimationID.HUMAN_SWORD_SLASH_WITHDAGGER:
			case AnimationID.HUMAN_SCYTHE_SLASH:
			case AnimationID.HUMAN_SCYTHE_LUNGE:
			case AnimationID.HUMAN_SCYTHE_SPIN:
			case AnimationID.HUMAN_SCYTHE_SWEEP:
			case AnimationID.HUMAN_FARMERSFORK_STAB:
			case AnimationID.HUMAN_BANNER_SPIKE:
			case AnimationID.HUMAN_RUBBER_CHICKEN_ATTACK:
			case AnimationID.HUMAN_CARROT_SWORD_ATTACK:
			case AnimationID.HUMAN_SKI_ATTACK:
			case AnimationID.HUMAN_CARD_ATTACK01:
			case AnimationID.HUMAN_XMAS25_CHRISTMAS_DINNER_ATTACK:
				return Kind.MELEE;

			case AnimationID.XBOWS_HUMAN_FIRE_AND_RELOAD:
			case AnimationID.XBOWS_HUMAN_FIRE_AND_RELOAD_PVN:
			case AnimationID.XBOWS_HUMAN_FIRE_AND_RELOAD_NO_STALL:
			case AnimationID.DTTD_PLAYER_FIRE_BONE_CROSSBOW_PVN:
			case AnimationID.HUNTER_XBOW_LAUNCH:
			case AnimationID.BALLISTA_ATTACK:
			case AnimationID.BALLISTA_ATTACK_PVN:
			case AnimationID.BALLISTA02_ATTACK:
			case AnimationID.BALLISTA02_ATTACK_PVN:
			case AnimationID.HUMAN_CHINCHOMPA_ATTACK_PVN:
			case AnimationID.II_HUMAN_DART_THROW:
			case AnimationID.II_HUMAN_DART_THROW_PVN:
			case AnimationID.OGRE_LONGBOW:
			case AnimationID._100_JUBBLY_OGRE_BOW:
			case AnimationID.HUMAN_GLAIVE_RALOS01_UNCHARGED_THROW:
			case AnimationID.HUMAN_GLAIVE_RALOS01_CHARGED_THROW:
			case AnimationID.CAMPHOR_BLOWPIPE_ATTACK:
			case AnimationID.IRONWOOD_BLOWPIPE_ATTACK:
			case AnimationID.ROSEWOOD_BLOWPIPE_ATTACK:
				return Kind.RANGED;

			case AnimationID.SLAYER_MAGICDART_CAST:
			case AnimationID.ZAROS_VERTICAL_CASTING_PRIORITY:
			case AnimationID.HUMAN_CASTSTRIKE_WALKMERGE:
			case AnimationID.HUMAN_CASTWAVE_WALKMERGE:
			case AnimationID.HUMAN_CAST_SURGE_WALKMERGE:
			case AnimationID.HUMAN_IBANS_ORN_CAST:
				return Kind.SPELL;

			case AnimationID.HUMAN_CASTSTRIKE_STAFF_WALKMERGE:
			case AnimationID.HUMAN_CASTWAVE_STAFF_WALKMERGE:
			case AnimationID.HUMAN_IBANS_ORN_SEA_CAST:
			case AnimationID.HUMAN_IBANS_ORN_SWAMP_CAST:
				return Kind.STAFF_CAST;

			case AnimationID.HUMAN_UNARMEDPUNCH:
			case AnimationID.HUMAN_UNARMEDKICK:
				return Kind.UNARMED;
			case AnimationID.HUMAN_BOW:
			case AnimationID.HUMAN_CROSSBOW:
			case AnimationID.HUMAN_THROW:
			case AnimationID.HUMAN_THROW_DART1:
			case AnimationID.HUMAN_THROW_DART2:
			case AnimationID.HUMAN_CHINCHOMPA_ATTACK:
			case AnimationID.BARROWS_REPEATING_CROSSBOW_FIRE:
			case AnimationID.DTTD_PLAYER_FIRE_BONE_CROSSBOW:
			case AnimationID.SNAKEBOSS_BLOWPIPE_ATTACK:
			case AnimationID.SNAKEBOSS_BLOWPIPE_ATTACK_ORNAMENT:
			case AnimationID.HUMAN_WEAPON_BOW_VENATOR01_SHOOT:
			case AnimationID.HUMAN_ATLATL_ATTACK_RANGED_01:
				return Kind.RANGED;
			case AnimationID.HUMAN_CASTSTRIKE:
			case AnimationID.HUMAN_CASTWAVE:
			case AnimationID.HUMAN_CAST_SURGE:
			case AnimationID.HUMAN_CASTIBANBLAST:
			case AnimationID.HUMAN_CASTING:
			case AnimationID.HUMAN_CASTCRUMBLEUNDEAD:
			case AnimationID.ZAROS_CASTING:
			case AnimationID.ZAROS_VERTICAL_CASTING:
			case AnimationID.HUMAN_SPELLCAST_GRASP:
			case AnimationID.HUMAN_SPELLCAST_DEMONBANE:
				return Kind.SPELL;
			case AnimationID.HUMAN_CASTSTRIKE_STAFF:
			case AnimationID.HUMAN_CASTWAVE_STAFF:
			case AnimationID.HUMAN_CASTCRUMBLEUNDEAD_STAFF:
			case AnimationID.TOA_SPELL_TUMEKEN01_CAST01:
				return Kind.STAFF_CAST;
			case AnimationID.HUMAN_CAST_SURGE_FAST:
				return Kind.FAST_SPELL;
			case AnimationID.HUMAN_DDAGGER_LUNGE:
			case AnimationID.HUMAN_DDAGGER_HACK:
			case AnimationID.HUMAN_DSPEAR_SLASH:
			case AnimationID.HUMAN_DSPEAR_STAB:
			case AnimationID.HUMAN_DSPEAR_LUNGE:
			case AnimationID.HUMAN_SWORD_STAB:
			case AnimationID.HUMAN_SWORD_SLASH:
			case AnimationID.HUMAN_SWORD_LUNGE:
			case AnimationID.HUMAN_AXE_CHOP:
			case AnimationID.HUMAN_AXE_HACK:
			case AnimationID.HUMAN_AXE_SMASH:
			case AnimationID.HUMAN_BLUNT_SPIKE:
			case AnimationID.HUMAN_BLUNT_POUND:
			case AnimationID.HUMAN_BLUNT_PUMMEL:
			case AnimationID.HUMAN_DHSWORD_STAB:
			case AnimationID.HUMAN_DHSWORD_CHOP:
			case AnimationID.HUMAN_DHSWORD_SLASH:
			case AnimationID.HUMAN_DHSWORD_LUNGE:
			case AnimationID.HUMAN_DHSWORD_SPIN:
			case AnimationID.HUMAN_STAFF_SPIKE:
			case AnimationID.HUMAN_STAFF_POUND:
			case AnimationID.HUMAN_STAFF_PUMMEL:
			case AnimationID.HUMAN_STAFFORB_SPIKE:
			case AnimationID.HUMAN_STAFFORB_POUND:
			case AnimationID.HUMAN_STAFFORB_PUMMEL:
			case AnimationID.HUMAN_SPEAR_SPIKE:
			case AnimationID.HUMAN_SPEAR_LUNGE:
			case AnimationID.HUMAN_ZAMORAKSPEAR_LUNGE:
			case AnimationID.HUMAN_ZAMORAKSPEAR_STAB:
			case AnimationID.HUMAN_ZAMORAKSPEAR_SLASH:
			case AnimationID.SLAYER_ABYSSAL_WHIP_ATTACK:
			case AnimationID.ABYSSAL_TENTACLE_ATTACK:
			case AnimationID.BARROW_GUTHAN_CRUSH:
			case AnimationID.BARROW_DHAROK_SLASH:
			case AnimationID.BARROW_DHAROK_CRUSH:
			case AnimationID.BARROW_TORAG_CRUSH:
			case AnimationID.HUMAN_ELDER_MAUL_ATTACK:
			case AnimationID.SCYTHE_OF_VITUR_ATTACK:
			case AnimationID.GHRAZI_RAPIER_ATTACK:
			case AnimationID.HUMAN_OSMUMTENS_FANG:
				return Kind.MELEE;
			default:
				return Kind.NONE;
		}
	}

	enum Kind
	{
		NONE, UNARMED, MELEE, RANGED, SPELL, STAFF_CAST, FAST_SPELL, WEAPON_RATE;

		int period(int weaponSpeed, boolean rapid)
		{
			switch (this)
			{
				case UNARMED:
				case FAST_SPELL:
					return 4;
				case SPELL:
					return 5;
				case STAFF_CAST:

					return weaponSpeed > 0 ? Math.min(5, weaponSpeed) : 0;
				case RANGED:
					return weaponSpeed > 0 ? Math.max(1, weaponSpeed - (rapid ? 1 : 0)) : 0;
				case MELEE:
				case WEAPON_RATE:
					return Math.max(0, weaponSpeed);
				default:
					return 0;
			}
		}
	}
}
