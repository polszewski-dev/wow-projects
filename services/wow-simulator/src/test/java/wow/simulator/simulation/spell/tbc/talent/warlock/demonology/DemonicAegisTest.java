package wow.simulator.simulation.spell.tbc.talent.warlock.demonology;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.test.commons.AbilityNames.FEL_ARMOR;
import static wow.test.commons.TalentNames.DEMONIC_AEGIS;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class DemonicAegisTest extends TbcWarlockSpellSimulationTest {
	/*
	Increases the effectiveness of your Demon Armor and Fel Armor spells by 30%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void spellDamageIsIncreased(int rank) {
		enableTalent(DEMONIC_AEGIS, rank);

		player.cast(FEL_ARMOR);

		updateUntil(30);

		var sdBefore = statsAt(0).getSpellDamage();
		var sdAfter = statsAt(1).getSpellDamage();

		assertThat(sdAfter).isEqualTo(sdBefore + 100 + 10 * rank);
	}
}
