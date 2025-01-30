package wow.simulator.simulation.spell.set.priest;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SHADOW_WORD_PAIN;

/**
 * User: POlszewski
 * Date: 2025-01-24
 */
class T6P2BonusTest extends PriestSpellSimulationTest {
	/*
	Increases the duration of your Shadow Word: Pain ability by 3 sec.
	*/

	@Test
	void test() {
		player.cast(SHADOW_WORD_PAIN);

		updateUntil(30);

		assertEffectDuration(SHADOW_WORD_PAIN, target, SHADOW_WORD_PAIN_INFO.duration() + 3);
	}

	@Override
	protected void afterSetUp() {
		equip("Handguards of Absolution");
		equip("Hood of Absolution");
	}
}