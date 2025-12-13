package wow.simulator.simulation.spell.tbc.talent.paladin.retribution;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.paladin.TbcPaladinTalentSimulationTest;

import static wow.test.commons.AbilityNames.HOLY_SHOCK;
import static wow.test.commons.AbilityNames.SANCTITY_AURA;
import static wow.test.commons.TalentNames.IMPROVED_SANCTITY_AURA;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ImprovedSanctityAuraTest extends TbcPaladinTalentSimulationTest {
	/*
	The amount of damage caused by targets affected by Sanctity Aura is increased by 2%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void damage_is_increased(int rank) {
		enableTalent(player, SANCTITY_AURA, 1);

		player.cast(SANCTITY_AURA);

		simulateTalent(IMPROVED_SANCTITY_AURA, rank, HOLY_SHOCK);

		assertDamageIsIncreasedByPct(10 + rank);
	}
}
