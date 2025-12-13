package wow.simulator.simulation.spell.tbc.talent.mage.arcane;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FROSTBOLT;
import static wow.test.commons.TalentNames.SPELL_POWER;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class SpellPowerTest extends TbcMageTalentSimulationTest {
	/*
	Increases critical strike damage bonus of all spells by 50%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void crit_damage_bonus_is_increased(int rank) {
		critsOnlyOnFollowingRolls(0);

		simulateTalent(SPELL_POWER, rank, FROSTBOLT);

		assertCritDamageBonusIsIncreasedByPct(25 * rank);
	}
}
