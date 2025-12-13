package wow.simulator.simulation.spell.tbc.talent.mage.fire;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.test.commons.AbilityNames.FIREBALL;
import static wow.test.commons.TalentNames.MOLTEN_FURY;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class MoltenFuryTest extends TbcMageTalentSimulationTest {
	/*
	Increases damage of all spells against targets with less than 20% health by 20%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void damage_is_increased(int rank) {
		setHealthPct(player.getTarget(), 19);

		simulateTalent(MOLTEN_FURY, rank, FIREBALL);

		assertDamageIsIncreasedByPct(10 * rank);
	}
}
