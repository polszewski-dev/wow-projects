package wow.simulator.simulation.spell.talent.priest.discipline;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.DIVINE_SPIRIT;
import static wow.commons.model.spell.AbilityId.SHADOW_WORD_PAIN;
import static wow.commons.model.talent.TalentId.IMPROVED_DIVINE_SPIRIT;

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
		enableTalent(TalentId.DIVINE_SPIRIT, 1);
		enableTalent(IMPROVED_DIVINE_SPIRIT, rank);

		player.cast(DIVINE_SPIRIT);
		player.cast(SHADOW_WORD_PAIN);

		updateUntil(30);

		var totalSpirit = player.getStats().getSpirit();
		var totalSp = getPercentOf(5 * rank, totalSpirit);

		assertDamageDone(SHADOW_WORD_PAIN, SHADOW_WORD_PAIN_INFO.damage(totalSp));
	}
}
