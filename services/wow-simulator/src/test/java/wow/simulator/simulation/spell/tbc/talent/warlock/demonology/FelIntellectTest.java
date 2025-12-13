package wow.simulator.simulation.spell.tbc.talent.warlock.demonology;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.TalentNames.FEL_INTELLECT;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class FelIntellectTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases the Intellect of your Imp, Voidwalker, Succubus, Incubus, Felhunter and Felguard by 15% and increases your maximum mana by 3%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void max_mana_is_increased(int rank) {
		assertMaxManaIsIncreasedByPct(FEL_INTELLECT, rank, rank);
	}
}
