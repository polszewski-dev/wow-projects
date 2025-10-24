package wow.simulator.simulation.spell.tbc.talent.priest.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.simulator.util.CalcUtils.getPercentOf;
import static wow.test.commons.TalentNames.SPIRITUAL_GUIDANCE;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class SpiritualGuidanceTest extends TbcPriestSpellSimulationTest {
	/*
	Increases spell damage and healing by up to 25% of your total Spirit.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void damageIsIncreased(int rank) {
		var spBefore = player.getStats().getSpellPower();

		enableTalent(SPIRITUAL_GUIDANCE, rank);

		var spAfter = player.getStats().getSpellPower();
		var totalSpirit = player.getStats().getSpirit();
		var spiritSpBonus = getPercentOf(5 * rank, totalSpirit);

		assertThat(spAfter).isEqualTo(spBefore + spiritSpBonus);
	}
}
