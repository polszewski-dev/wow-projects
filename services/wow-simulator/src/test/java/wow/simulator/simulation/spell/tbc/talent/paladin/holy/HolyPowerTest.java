package wow.simulator.simulation.spell.tbc.talent.paladin.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.paladin.TbcPaladinTalentSimulationTest;

import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.test.commons.AbilityNames.EXORCISM;
import static wow.test.commons.TalentNames.HOLY_POWER;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class HolyPowerTest extends TbcPaladinTalentSimulationTest {
	/*
	Increases the critical effect chance of your Holy spells by 5%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void crit_chance_is_increased(int rank) {
		simulateTalent(HOLY_POWER, rank, EXORCISM);

		assertCritChanceIsIncreasedByPct(rank);
	}

	@Override
	protected void beforeSetUp() {
		super.beforeSetUp();
		enemyType = UNDEAD;
	}
}
