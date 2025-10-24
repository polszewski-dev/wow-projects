package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.CORRUPTION;
import static wow.test.commons.AbilityNames.CURSE_OF_THE_ELEMENTS;
import static wow.test.commons.TalentNames.MALEDICTION;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class MaledictionTest extends TbcWarlockSpellSimulationTest {
	/*
	Increases the damage bonus effect of your Curse of the Elements spell by an additional 3%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void damageIsIncreasedCorrectly(int rank) {
		enableTalent(MALEDICTION, rank);

		player.cast(CURSE_OF_THE_ELEMENTS);
		player.cast(CORRUPTION);

		updateUntil(30);

		assertDamageDone(CORRUPTION, CORRUPTION_INFO.damage(), 10 + rank);
	}
}
