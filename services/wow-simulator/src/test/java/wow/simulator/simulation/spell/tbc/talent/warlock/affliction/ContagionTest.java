package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.CORRUPTION;
import static wow.test.commons.AbilityNames.CURSE_OF_AGONY;
import static wow.test.commons.TalentNames.CONTAGION;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ContagionTest extends TbcWarlockSpellSimulationTest {
	/*
	Increases the damage of Curse of Agony, Corruption and Seed of Corruption by 5% and reduces the chance your Affliction spells will be dispelled by an additional 30%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void curseOfAgony(int rank) {
		enableTalent(CONTAGION, rank);

		player.cast(CURSE_OF_AGONY);
		updateUntil(30);

		assertDamageDone(CURSE_OF_AGONY, CURSE_OF_AGONY_INFO.damage(), rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void corruption(int rank) {
		enableTalent(CONTAGION, rank);

		player.cast(CORRUPTION);
		updateUntil(30);

		assertDamageDone(CORRUPTION, CORRUPTION_INFO.damage(), rank);
	}
}
