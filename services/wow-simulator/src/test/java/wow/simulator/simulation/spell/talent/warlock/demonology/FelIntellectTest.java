package wow.simulator.simulation.spell.talent.warlock.demonology;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.test.commons.TalentNames.FEL_INTELLECT;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class FelIntellectTest extends WarlockSpellSimulationTest {
	/*
	Increases the Intellect of your Imp, Voidwalker, Succubus, Incubus, Felhunter and Felguard by 15% and increases your maximum mana by 3%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void staminaBonus(int rank) {
		int maxManaBefore = player.getStats().getMaxMana();

		enableTalent(FEL_INTELLECT, rank);

		int maxManaAfter = player.getStats().getMaxMana();

		assertIsIncreasedByPct(maxManaAfter, maxManaBefore, rank);
	}
}
