package wow.simulator.simulation.spell.talent.priest.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.test.commons.AbilityNames.SMITE;
import static wow.test.commons.TalentNames.HOLY_SPECIALIZATION;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class HolySpecializationTest extends PriestSpellSimulationTest {
	/*
	Increases the critical effect chance of your Holy spells by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void critIsIncreased(int rank) {
		var spellCritPctBefore = player.getStats().getSpellCritPct();

		enableTalent(HOLY_SPECIALIZATION, rank);

		player.cast(SMITE);

		updateUntil(30);

		assertLastCritChance(spellCritPctBefore + rank);
	}
}
