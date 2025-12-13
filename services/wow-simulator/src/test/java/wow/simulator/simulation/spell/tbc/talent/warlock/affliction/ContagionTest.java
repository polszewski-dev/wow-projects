package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.CORRUPTION;
import static wow.test.commons.AbilityNames.CURSE_OF_AGONY;
import static wow.test.commons.TalentNames.CONTAGION;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ContagionTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases the damage of Curse of Agony, Corruption and Seed of Corruption by 5% and reduces the chance your Affliction spells will be dispelled by an additional 30%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void coa_damage_is_increased(int rank) {
		simulateTalent(CONTAGION, rank, CURSE_OF_AGONY);

		assertDamageIsIncreasedByPct(rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void corruption_damage_is_increased(int rank) {
		simulateTalent(CONTAGION, rank, CORRUPTION);

		assertDamageIsIncreasedByPct(rank);
	}
}
