package wow.simulator.simulation.spell;

import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.character.RaceId.ORC;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class WarlockSpellSimulationTest extends SpellSimulationTest {
	@Override
	protected void beforeSetUp() {
		characterClassId = WARLOCK;
		raceId = ORC;
	}

	protected static final SpellInfo CORRUPTION_INFO = new SpellInfo(370, 2).withPeriodic(900, 93.60, 18, 6);
	protected static final SpellInfo CURSE_OF_AGONY_INFO = new SpellInfo(265, 0).withPeriodic(1356, 120, 24, 12);
	protected static final SpellInfo CURSE_OF_DOOM_INFO = new SpellInfo(380, 0).withPeriodic(4200, 200, 60, 1);
	protected static final SpellInfo DEATH_COIL_INFO = new SpellInfo(600, 0).withDirect(526, 526, 21.42);
	protected static final SpellInfo DRAIN_LIFE_INFO = new SpellInfo(425, 5).withPeriodic(540, 71.43, 5, 5);
	protected static final SpellInfo LIFE_TAP_INFO = new SpellInfo(0, 0).withDirect(582, 582, 80);
	protected static final SpellInfo SIPHON_LIFE_INFO = new SpellInfo(410, 0).withPeriodic(630, 100, 30, 10);
	protected static final SpellInfo UNSTABLE_AFFLICTION_INFO = new SpellInfo(400, 1.5).withPeriodic(1050, 120, 18, 6);

	protected static final SpellInfo CONFLAGRATE_INFO = new SpellInfo(305, 0).withDirect(579, 721, 42.86);
	protected static final SpellInfo IMMOLATE_INFO = new SpellInfo(445, 2).withDirect(332, 332, 18.65).withPeriodic(615, 63.63, 15, 5);
	protected static final SpellInfo INCINERATE_INFO = new SpellInfo(355, 2.5).withDirect(444, 514, 71.43);
	protected static final SpellInfo INCINERATE_WITH_BONUS_INFO = new SpellInfo(0, 0).withDirect(444 + 111, 514 + 128, 0);
	protected static final SpellInfo SEARING_PAIN_INFO = new SpellInfo(205, 1.5).withDirect(270, 320, 42.86);
	protected static final SpellInfo SHADOW_BOLT_INFO = new SpellInfo(420, 3).withDirect(544, 607, 85.71);
	protected static final SpellInfo SHADOWBURN_INFO = new SpellInfo(435, 0).withDirect(597, 665, 42.86);
	protected static final SpellInfo SOUL_FIRE_INFO = new SpellInfo(250, 6).withDirect(1003, 1257, 115);
}
