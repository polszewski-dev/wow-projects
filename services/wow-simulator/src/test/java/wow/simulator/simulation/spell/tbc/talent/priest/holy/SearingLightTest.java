package wow.simulator.simulation.spell.tbc.talent.priest.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.test.commons.AbilityNames.HOLY_FIRE;
import static wow.test.commons.AbilityNames.SMITE;
import static wow.test.commons.TalentNames.SEARING_LIGHT;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class SearingLightTest extends TbcPriestSpellSimulationTest {
	/*
	Increases the damage of your Smite and Holy Fire spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void smiteDamageIsIncreased(int rank) {
		enableTalent(SEARING_LIGHT, rank);

		player.cast(SMITE);

		updateUntil(30);

		assertDamageDone(SMITE, SMITE_INFO.damage(), 5 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void holyFireDamageIsIncreased(int rank) {
		enableTalent(SEARING_LIGHT, rank);

		player.cast(HOLY_FIRE);

		updateUntil(30);

		assertDamageDone(HOLY_FIRE, HOLY_FIRE_INFO.damage(), 5 * rank);
	}
}
