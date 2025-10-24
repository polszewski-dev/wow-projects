package wow.simulator.simulation.spell.vanilla;

import wow.simulator.util.SpellInfo;

/**
 * User: POlszewski
 * Date: 2025-02-18
 */
public interface VanillaSpellInfos {
	SpellInfo SHOOT_INFO = new SpellInfo(0, 0).withDirect(0, 0, 0);

	SpellInfo STARSHARDS_INFO = new SpellInfo(0, 0).withPeriodic(785, 83.5, 15, 5);

	SpellInfo HOLY_FIRE_INFO = new SpellInfo(290, 3.5).withDirect(426, 537, 85.71).withPeriodic(165, 16.5, 10, 5);
	SpellInfo SMITE_INFO = new SpellInfo(385, 2.5).withDirect(549, 616, 71.42);

	SpellInfo DEVOURING_PLAGUE_INFO = new SpellInfo(11145, 0).withPeriodic(1216, 80, 24, 8);
	SpellInfo MIND_BLAST_INFO = new SpellInfo(450, 1.5).withDirect(711, 752, 42.85);
	SpellInfo MIND_FLAY_INFO = new SpellInfo(230, 3).withPeriodic(528, 57, 3, 3);
	SpellInfo SHADOW_WORD_PAIN_INFO = new SpellInfo(575, 0).withPeriodic(1236, 110, 18, 6);

	SpellInfo CORRUPTION_INFO = new SpellInfo(370, 2).withPeriodic(900, 93.60, 18, 6);
	SpellInfo CURSE_OF_AGONY_INFO = new SpellInfo(265, 0).withPeriodic(1356, 120, 24, 12);
	SpellInfo CURSE_OF_DOOM_INFO = new SpellInfo(380, 0).withPeriodic(4200, 200, 60, 1);
	SpellInfo DEATH_COIL_INFO = new SpellInfo(600, 0).withDirect(526, 526, 21.42);
	SpellInfo DRAIN_LIFE_INFO = new SpellInfo(425, 5).withPeriodic(540, 71.43, 5, 5);
	SpellInfo LIFE_TAP_INFO = new SpellInfo(0, 0).withDirect(582, 582, 80);
	SpellInfo SIPHON_LIFE_INFO = new SpellInfo(410, 0).withPeriodic(630, 100, 30, 10);

	SpellInfo CONFLAGRATE_INFO = new SpellInfo(305, 0).withDirect(579, 721, 42.86);
	SpellInfo IMMOLATE_INFO = new SpellInfo(445, 2).withDirect(332, 332, 18.65).withPeriodic(615, 63.63, 15, 5);
	SpellInfo SEARING_PAIN_INFO = new SpellInfo(205, 1.5).withDirect(270, 320, 42.86);
	SpellInfo SHADOW_BOLT_INFO = new SpellInfo(420, 3).withDirect(544, 607, 85.71);
	SpellInfo SHADOWBURN_INFO = new SpellInfo(435, 0).withDirect(597, 665, 42.86);
	SpellInfo SOUL_FIRE_INFO = new SpellInfo(250, 6).withDirect(1003, 1257, 115);
}
