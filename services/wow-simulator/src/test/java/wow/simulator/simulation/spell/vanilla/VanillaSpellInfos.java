package wow.simulator.simulation.spell.vanilla;

import wow.simulator.util.SpellInfo;

import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-02-18
 */
public interface VanillaSpellInfos {
	SpellInfo SHOOT_INFO = new SpellInfo(SHOOT, 0, 0).withDirect(0, 0, 0);

	SpellInfo STARSHARDS_INFO = new SpellInfo(STARSHARDS, 0, 0).withPeriodic(785, 83.5, 15, 5);

	SpellInfo HOLY_FIRE_INFO = new SpellInfo(HOLY_FIRE, 290, 3.5).withDirect(426, 537, 85.71).withPeriodic(165, 16.5, 10, 5);
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
	SpellInfo INCINERATE_WITH_BONUS_INFO = new SpellInfo(INCINERATE, 0, 0).withDirect(444 + 111, 514 + 128, 0);
	SpellInfo SEARING_PAIN_INFO = new SpellInfo(SEARING_PAIN, 205, 1.5).withDirect(270, 320, 42.86);
	SpellInfo SHADOW_BOLT_INFO = new SpellInfo(SHADOW_BOLT, 420, 3).withDirect(544, 607, 85.71);
	SpellInfo SHADOWBURN_INFO = new SpellInfo(SHADOWBURN, 435, 0).withDirect(597, 665, 42.86);
	SpellInfo SOUL_FIRE_INFO = new SpellInfo(SOUL_FIRE, 250, 6).withDirect(1003, 1257, 115);

	SpellInfo FIREBALL_INFO = new SpellInfo(FIREBALL, 465, 3.5).withDirect(717, 913, 100).withPeriodic(84, 0, 8, 4);
	SpellInfo FIRE_BLAST_INFO = new SpellInfo(FIRE_BLAST, 465, 0).withDirect(664, 786, 42.86);
	SpellInfo FROSTBOLT_INFO = new SpellInfo(FROSTBOLT, 345, 3).withDirect(630, 680, 81.43);

	SpellInfo ENTANGLING_ROOTS_INFO = new SpellInfo(ENTANGLING_ROOTS, 160, 1.5).withPeriodic(351, 90, 27, 9);
}
