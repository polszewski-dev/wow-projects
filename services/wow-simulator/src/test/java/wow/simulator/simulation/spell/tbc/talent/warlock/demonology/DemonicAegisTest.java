package wow.simulator.simulation.spell.tbc.talent.warlock.demonology;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.character.model.snapshot.StatSummary;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.FEL_ARMOR;
import static wow.test.commons.TalentNames.DEMONIC_AEGIS;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class DemonicAegisTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases the effectiveness of your Demon Armor and Fel Armor spells by 30%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void fel_armor_spell_damage_bonus_is_increased(int rank) {
		simulateTalent(DEMONIC_AEGIS, rank, FEL_ARMOR);

		assertStatBonusIsIncreasedByPct(StatSummary::getSpellDamage, 10 * rank);
	}
}
