package wow.simulator.simulation.spell;

import static wow.commons.model.character.CharacterClassId.PRIEST;
import static wow.commons.model.character.RaceId.UNDEAD;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
public abstract class PriestSpellSimulationTest extends SpellSimulationTest {
	@Override
	protected void beforeSetUp() {
		characterClassId = PRIEST;
		raceId = UNDEAD;
	}

	protected static final SpellInfo SHOOT_INFO = new SpellInfo(0, 0).withDirect(0, 0, 0);

	protected static final SpellInfo STARSHARDS_INFO = new SpellInfo(0, 0).withPeriodic(785, 83.5, 15, 5);

	protected static final SpellInfo HOLY_FIRE_INFO = new SpellInfo(290, 3.5).withDirect(426, 537, 85.71).withPeriodic(165, 16.5, 10, 5);
	protected static final SpellInfo SMITE_INFO = new SpellInfo(385, 2.5).withDirect(549, 616, 71.42);

	protected static final SpellInfo DEVOURING_PLAGUE_INFO = new SpellInfo(11145, 0).withPeriodic(1216, 80, 24, 8);
	protected static final SpellInfo MIND_BLAST_INFO = new SpellInfo(450, 1.5).withDirect(711, 752, 42.85);
	protected static final SpellInfo MIND_FLAY_INFO = new SpellInfo(230, 3).withPeriodic(528, 57, 3, 3);
	protected static final SpellInfo SHADOW_WORD_DEATH_INFO = new SpellInfo(309, 0).withDirect(572, 664, 42.85);
	protected static final SpellInfo SHADOW_WORD_PAIN_INFO = new SpellInfo(575, 0).withPeriodic(1236, 110, 18, 6);
	protected static final SpellInfo VAMPIRIC_TOUCH_INFO = new SpellInfo(425, 0).withPeriodic(650, 100, 15, 5);
}
