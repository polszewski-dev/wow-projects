package wow.simulator.simulation.spell.tbc.talent.paladin.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.paladin.TbcPaladinTalentSimulationTest;

import static wow.commons.model.character.CreatureType.UNDEAD;
import static wow.test.commons.AbilityNames.EXORCISM;
import static wow.test.commons.TalentNames.PURIFYING_POWER;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class PurifyingPowerTest extends TbcPaladinTalentSimulationTest {
	/*
	Reduces the mana cost of your Cleanse, Purify and Consecration spells by 10% and increases the critical strike chance of your Exorcism and Holy Wrath spells by 20%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void exorcism_crit_chance_is_increased(int rank) {
		simulateTalent(PURIFYING_POWER, rank, EXORCISM);

		assertCritChanceIsIncreasedByPct(10 * rank);
	}

	@Override
	protected void beforeSetUp() {
		super.beforeSetUp();
		enemyType = UNDEAD;
	}
}
