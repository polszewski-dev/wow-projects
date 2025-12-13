package wow.simulator.simulation.spell.tbc.talent.mage.arcane;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FIREBALL;
import static wow.test.commons.TalentNames.ARCANE_INSTABILITY;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ArcaneInstabilityTest extends TbcMageTalentSimulationTest {
	/*
	Increases your spell damage and critical strike chance by 3%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void damage_is_increased(int rank) {
		simulateTalent(ARCANE_INSTABILITY, rank, FIREBALL);

		assertDamageIsIncreasedByPct(rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void crit_chance_is_increased(int rank) {
		simulateTalent(ARCANE_INSTABILITY, rank, FIREBALL);

		assertCritChanceIsIncreasedByPct(rank);
	}
}
