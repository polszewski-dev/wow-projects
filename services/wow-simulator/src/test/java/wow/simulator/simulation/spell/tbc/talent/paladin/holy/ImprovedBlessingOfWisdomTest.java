package wow.simulator.simulation.spell.tbc.talent.paladin.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.character.model.snapshot.StatSummary;
import wow.simulator.simulation.spell.tbc.talent.paladin.TbcPaladinTalentSimulationTest;

import static wow.test.commons.AbilityNames.BLESSING_OF_WISDOM;
import static wow.test.commons.TalentNames.IMPROVED_BLESSING_OF_WISDOM;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ImprovedBlessingOfWisdomTest extends TbcPaladinTalentSimulationTest {
	/*
	Increases the effect of your Blessing of Wisdom spell by 20%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void mp5_bonus_is_increased(int rank) {
		simulateTalent(IMPROVED_BLESSING_OF_WISDOM, rank, BLESSING_OF_WISDOM);

		assertStatBonusIsIncreasedByPct(StatSummary::getInterruptedManaRegen, 10 * rank);
	}
}
