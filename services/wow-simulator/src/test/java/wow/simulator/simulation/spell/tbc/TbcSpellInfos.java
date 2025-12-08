package wow.simulator.simulation.spell.tbc;

import wow.simulator.util.SpellInfo;

import java.util.List;

import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-02-18
 */
public interface TbcSpellInfos {
	SpellInfo SHOOT_INFO = new SpellInfo(SHOOT, 0, 0).withDirect(0, 0, 0);

	SpellInfo STARSHARDS_INFO = new SpellInfo(STARSHARDS, 0, 0).withPeriodic(785, 83.5, 15, 5);

	SpellInfo GREATER_HEAL_INFO = new SpellInfo(GREATER_HEAL, 825, 3).withDirect(2414, 2803, 85.71);
	SpellInfo HOLY_FIRE_INFO = new SpellInfo(HOLY_FIRE, 290, 3.5).withDirect(426, 537, 85.71).withPeriodic(165, 16.5, 10, 5);
	SpellInfo RENEW_INFO = new SpellInfo(RENEW, 450, 0).withPeriodic(1110, 100, 15, 5);
	SpellInfo SMITE_INFO = new SpellInfo(SMITE, 385, 2.5).withDirect(549, 616, 71.42);

	SpellInfo DEVOURING_PLAGUE_INFO = new SpellInfo(DEVOURING_PLAGUE, 1145, 0).withPeriodic(1216, 80, 24, 8);
	SpellInfo MIND_BLAST_INFO = new SpellInfo(MIND_BLAST, 450, 1.5).withDirect(711, 752, 42.85);
	SpellInfo MIND_FLAY_INFO = new SpellInfo(MIND_FLAY, 230, 3).withPeriodic(528, 57, 3, 3);
	SpellInfo SHADOW_WORD_DEATH_INFO = new SpellInfo(SHADOW_WORD_DEATH, 309, 0).withDirect(572, 664, 42.85);
	SpellInfo SHADOW_WORD_PAIN_INFO = new SpellInfo(SHADOW_WORD_PAIN, 575, 0).withPeriodic(1236, 110, 18, 6);
	SpellInfo VAMPIRIC_TOUCH_INFO = new SpellInfo(VAMPIRIC_TOUCH, 425, 0).withPeriodic(650, 100, 15, 5);

	SpellInfo CORRUPTION_INFO = new SpellInfo(CORRUPTION, 370, 2).withPeriodic(900, 93.60, 18, 6);
	SpellInfo CURSE_OF_AGONY_INFO = new SpellInfo(CURSE_OF_AGONY, 265, 0).withPeriodic(1356, 120, 24, 12);
	SpellInfo CURSE_OF_DOOM_INFO = new SpellInfo(CURSE_OF_DOOM, 380, 0).withPeriodic(4200, 200, 60, 1);
	SpellInfo DEATH_COIL_INFO = new SpellInfo(DEATH_COIL, 600, 0).withDirect(526, 526, 21.42);
	SpellInfo DRAIN_LIFE_INFO = new SpellInfo(DRAIN_LIFE, 425, 5).withPeriodic(540, 71.43, 5, 5);
	SpellInfo LIFE_TAP_INFO = new SpellInfo(LIFE_TAP, 0, 0).withDirect(582, 582, 80);
	SpellInfo SIPHON_LIFE_INFO = new SpellInfo(SIPHON_LIFE, 410, 0).withPeriodic(630, 100, 30, 10);
	SpellInfo UNSTABLE_AFFLICTION_INFO = new SpellInfo(UNSTABLE_AFFLICTION, 400, 1.5).withPeriodic(1050, 120, 18, 6);

	SpellInfo CONFLAGRATE_INFO = new SpellInfo(CONFLAGRATE, 305, 0).withDirect(579, 721, 42.86);
	SpellInfo IMMOLATE_INFO = new SpellInfo(IMMOLATE, 445, 2).withDirect(332, 332, 18.65).withPeriodic(615, 63.63, 15, 5);
	SpellInfo INCINERATE_INFO = new SpellInfo(INCINERATE, 355, 2.5).withDirect(444, 514, 71.43);
	SpellInfo INCINERATE_WITH_BONUS_INFO = new SpellInfo(INCINERATE, 355, 2.5).withDirect(444 + 111, 514 + 128, 71.43);
	SpellInfo SEARING_PAIN_INFO = new SpellInfo(SEARING_PAIN, 205, 1.5).withDirect(270, 320, 42.86);
	SpellInfo SHADOW_BOLT_INFO = new SpellInfo(SHADOW_BOLT, 420, 3).withDirect(544, 607, 85.71);
	SpellInfo SHADOWBURN_INFO = new SpellInfo(SHADOWBURN, 435, 0).withDirect(597, 665, 42.86);
	SpellInfo SOUL_FIRE_INFO = new SpellInfo(SOUL_FIRE, 250, 6).withDirect(1003, 1257, 115);

	SpellInfo ARCANE_BLAST_INFO = new SpellInfo(ARCANE_BLAST, 195, 2.5).withDirect(668, 772, 71.43);
	SpellInfo ARCANE_EXPLOSION_INFO = new SpellInfo(ARCANE_EXPLOSION, 545, 0).withDirect(377, 407, 21.28);
	SpellInfo ARCANE_MISSILES_INFO = new SpellInfo(ARCANE_MISSILES, 785, 0).withPeriodic(1430, 142.86, 5, 5);

	SpellInfo BLAST_WAVE_INFO = new SpellInfo(BLAST_WAVE, 620, 0).withDirect(616, 724, 13.57);
	SpellInfo DRAGONS_BREATH_INFO = new SpellInfo(DRAGONS_BREATH, 700, 0).withDirect(680, 790, 13.57);
	SpellInfo FIRE_BLAST_INFO = new SpellInfo(FIRE_BLAST, 465, 0).withDirect(664, 786, 42.86);
	SpellInfo FIREBALL_INFO = new SpellInfo(FIREBALL, 465, 3.5).withDirect(717, 913, 100).withPeriodic(84, 0, 8, 4);
	SpellInfo FLAMESTRIKE_INFO = new SpellInfo(FLAMESTRIKE, 1175, 3).withDirect(480, 585, 17.61).withPeriodic(424, 10.96, 8, 4);
	SpellInfo PYROBLAST_INFO = new SpellInfo(PYROBLAST, 500, 6).withDirect(939, 1191, 115).withPeriodic(356, 20, 12, 4);
	SpellInfo SCORCH_INFO = new SpellInfo(SCORCH, 180, 1.5).withDirect(305, 361, 42.86);

	SpellInfo BLIZZARD_INFO = new SpellInfo(BLIZZARD, 1645, 0).withPeriodic(1480, 76.19, 8, 8);
	SpellInfo CONE_OF_COLD_INFO = new SpellInfo(CONE_OF_COLD, 645, 0).withDirect(418, 457, 13.57);
	SpellInfo FROST_NOVA_INFO = new SpellInfo(FROST_NOVA, 185, 0).withDirect(100, 113, 13.57);
	SpellInfo FROSTBOLT_INFO = new SpellInfo(FROSTBOLT, 345, 3).withDirect(630, 680, 81.43);
	SpellInfo ICE_LANCE_INFO = new SpellInfo(ICE_LANCE, 150, 0).withDirect(173, 200, 14.29);

	SpellInfo ENTANGLING_ROOTS_INFO = new SpellInfo(ENTANGLING_ROOTS, 160, 1.5).withPeriodic(351, 90, 27, 9);
	SpellInfo HURRICANE_INFO = new SpellInfo(HURRICANE, 1905, 0).withPeriodic(2060, 128, 10, 10);
	SpellInfo INSECT_SWARM_INFO = new SpellInfo(INSECT_SWARM, 175, 0).withPeriodic(792, 76, 12, 6);
	SpellInfo MOONFIRE_INFO = new SpellInfo(MOONFIRE, 160, 1.5).withDirect(305, 357, 14.95).withPeriodic(600, 52.09, 12, 4);
	SpellInfo STARFIRE_INFO = new SpellInfo(STARFIRE, 370, 3.5).withDirect(550, 647, 100);
	SpellInfo WRATH_INFO = new SpellInfo(WRATH, 255, 2).withDirect(383, 432, 57.14);

	SpellInfo CHAIN_LIGHTNING_INFO = new SpellInfo(CHAIN_LIGHTNING, 760, 2).withDirect(734, 838, 71.42);
	SpellInfo EARTH_SHOCK_INFO = new SpellInfo(EARTH_SHOCK, 535, 0).withDirect(661, 696, 42.86);
	SpellInfo FLAME_SHOCK_INFO = new SpellInfo(FLAME_SHOCK, 500, 0).withDirect(377, 377, 14.95).withPeriodic(420, 52.09, 12, 3);
	SpellInfo FROST_SHOCK_INFO = new SpellInfo(FROST_SHOCK, 525, 0).withDirect(647, 683, 42.88);
	SpellInfo LIGHTNING_BOLT_INFO = new SpellInfo(LIGHTNING_BOLT, 300, 2.5).withDirect(571, 652, 79.4);

	SpellInfo CONSECRATION_INFO = new SpellInfo(CONSECRATION, 660, 0).withPeriodic(512, 95.24, 8, 1);
	SpellInfo EXORCISM_INFO = new SpellInfo(EXORCISM, 340, 0).withDirect(626, 698, 42.85);
	SpellInfo HAMMER_OF_WRATH_INFO = new SpellInfo(HAMMER_OF_WRATH, 440, 0.5).withDirect(672, 742, 42.86);
	SpellInfo HOLY_SHOCK_INFO = new SpellInfo(HOLY_SHOCK, 650, 0).withDirect(721, 779, 42.85);
	SpellInfo HOLY_SHOCK_HEALING_PART_INFO = new SpellInfo(HOLY_SHOCK, 650, 0).withDirect(913, 987, 42.85);
	SpellInfo HOLY_WRATH_INFO = new SpellInfo(HOLY_WRATH, 825, 2).withDirect(637, 748, 19.05);

	static List<Integer> spellDamageLevels() {
		return List.of(0, 100, 1000);
	}

	static List<Integer> spellHealingLevels() {
		return List.of(0, 100, 1000);
	}
}
