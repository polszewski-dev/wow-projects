package wow.simulator.simulation.spell.tbc.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.character.model.snapshot.StatSummary;
import wow.simulator.simulation.spell.tbc.talent.priest.TbcPriestTalentSimulationTest;
import wow.test.commons.TalentNames;

import static wow.test.commons.AbilityNames.DIVINE_SPIRIT;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ImprovedDivineSpiritTest extends TbcPriestTalentSimulationTest {
	/*
	Your Divine Spirit and Prayer of Spirit spells also increase the target's spell damage and healing by an amount equal to 10% of their total Spirit.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void damageIsIncreased(int rank) {
		simulateTalent(TalentNames.IMPROVED_DIVINE_SPIRIT, rank, DIVINE_SPIRIT);

		assertBonusStatConversion(StatSummary::getSpirit, StatSummary::getSpellPower, 5 * rank);
	}
}
