package wow.simulator.simulation.spell.tbc.talent.druid.restoration;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.character.model.snapshot.StatSummary;
import wow.simulator.simulation.spell.tbc.talent.druid.TbcDruidTalentSimulationTest;

import static wow.test.commons.AbilityNames.GIFT_OF_THE_WILD;
import static wow.test.commons.AbilityNames.MARK_OF_THE_WILD;
import static wow.test.commons.TalentNames.IMPROVED_MARK_OF_THE_WILD;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ImprovedMarkOfTheWildTest extends TbcDruidTalentSimulationTest {
	/*
	Increases the effects of your Mark of the Wild and Gift of the Wild spells by 35%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void marks_stamina_bonus_is_increased(int rank) {
		simulateTalent(IMPROVED_MARK_OF_THE_WILD, rank, MARK_OF_THE_WILD);

		assertStatBonusIsIncreasedByPct(StatSummary::getStamina, 7 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void marks_intellect_bonus_is_increased(int rank) {
		simulateTalent(IMPROVED_MARK_OF_THE_WILD, rank, MARK_OF_THE_WILD);

		assertStatBonusIsIncreasedByPct(StatSummary::getIntellect, 7 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void marks_spirit_bonus_is_increased(int rank) {
		simulateTalent(IMPROVED_MARK_OF_THE_WILD, rank, MARK_OF_THE_WILD);

		assertStatBonusIsIncreasedByPct(StatSummary::getSpirit, 7 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void gifts_stamina_bonus_is_increased(int rank) {
		simulateTalent(IMPROVED_MARK_OF_THE_WILD, rank, GIFT_OF_THE_WILD);

		assertStatBonusIsIncreasedByPct(StatSummary::getStamina, 7 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void gifts_intellect_bonus_is_increased(int rank) {
		simulateTalent(IMPROVED_MARK_OF_THE_WILD, rank, GIFT_OF_THE_WILD);

		assertStatBonusIsIncreasedByPct(StatSummary::getIntellect, 7 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void gifts_spirit_bonus_is_increased(int rank) {
		simulateTalent(IMPROVED_MARK_OF_THE_WILD, rank, GIFT_OF_THE_WILD);

		assertStatBonusIsIncreasedByPct(StatSummary::getSpirit, 7 * rank);
	}
}
