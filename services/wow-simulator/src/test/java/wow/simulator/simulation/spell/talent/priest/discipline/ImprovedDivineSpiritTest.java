package wow.simulator.simulation.spell.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.simulator.util.CalcUtils.getPercentOf;
import static wow.test.commons.AbilityNames.DIVINE_SPIRIT;
import static wow.test.commons.AbilityNames.SHADOW_WORD_PAIN;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ImprovedDivineSpiritTest extends PriestSpellSimulationTest {
	/*
	Your Divine Spirit and Prayer of Spirit spells also increase the target's spell damage and healing by an amount equal to 10% of their total Spirit.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void damageIsIncreased(int rank) {
		enableTalent(TalentNames.DIVINE_SPIRIT, 1);
		enableTalent(TalentNames.IMPROVED_DIVINE_SPIRIT, rank);

		player.cast(DIVINE_SPIRIT);
		player.cast(SHADOW_WORD_PAIN);

		updateUntil(30);

		var totalSpirit = player.getStats().getSpirit();
		var totalSp = getPercentOf(5 * rank, totalSpirit);

		assertDamageDone(SHADOW_WORD_PAIN, SHADOW_WORD_PAIN_INFO.damage(totalSp));
	}
}
