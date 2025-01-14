package wow.simulator.simulation.spell.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CORRUPTION;
import static wow.commons.model.spell.AbilityId.CURSE_OF_AGONY;
import static wow.commons.model.talent.TalentId.CONTAGION;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ContagionTest extends WarlockSpellSimulationTest {
	/*
	Increases the damage of Curse of Agony, Corruption and Seed of Corruption by 5% and reduces the chance your Affliction spells will be dispelled by an additional 30%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void curseOfAgony(int rank) {
		enableTalent(CONTAGION, rank);

		player.cast(CURSE_OF_AGONY);
		updateUntil(30);

		assertDamageDone(CURSE_OF_AGONY, 1356, rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void corruption(int rank) {
		enableTalent(CONTAGION, rank);

		player.cast(CORRUPTION);
		updateUntil(30);

		assertDamageDone(CORRUPTION, 900, rank);
	}
}
