package wow.simulator.simulation.spell.talent.warlock.demonology;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.CORRUPTION;
import static wow.commons.model.spell.AbilityId.FEL_ARMOR;
import static wow.commons.model.talent.TalentId.DEMONIC_AEGIS;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class DemonicAegisTest extends WarlockSpellSimulationTest {
	/*
	Increases the effectiveness of your Demon Armor and Fel Armor spells by 30%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void spellDamageIsIncreased(int rank) {
		enableTalent(DEMONIC_AEGIS, rank);

		player.cast(FEL_ARMOR);
		player.cast(CORRUPTION);

		updateUntil(30);

		assertDamageDone(CORRUPTION, CORRUPTION_INFO.damage(100 + 10 * rank));
	}
}
