package wow.simulator.simulation.spell.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SHADOW_WORD_PAIN;
import static wow.commons.model.talent.TalentId.MENTAL_AGILITY;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class MentalAgilityTest extends PriestSpellSimulationTest {
	/*
	Reduces the mana cost of your instant cast spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void manaCostIsReduced(int rank) {
		enableTalent(MENTAL_AGILITY, rank);

		player.cast(SHADOW_WORD_PAIN);

		updateUntil(30);

		assertManaPaid(SHADOW_WORD_PAIN, player, SHADOW_WORD_PAIN_INFO.manaCost(), -2 * rank);
	}
}
