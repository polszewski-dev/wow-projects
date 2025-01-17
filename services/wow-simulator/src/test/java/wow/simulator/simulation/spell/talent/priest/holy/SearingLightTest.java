package wow.simulator.simulation.spell.talent.priest.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.HOLY_FIRE;
import static wow.commons.model.spell.AbilityId.SMITE;
import static wow.commons.model.talent.TalentId.SEARING_LIGHT;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class SearingLightTest extends PriestSpellSimulationTest {
	/*
	Increases the damage of your Smite and Holy Fire spells by 10%.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void smiteDamageIsIncreased(int rank) {
		enableTalent(SEARING_LIGHT, rank);

		player.cast(SMITE);

		updateUntil(30);

		assertDamageDone(SMITE, 582.5, 5 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void holyFireDamageIsIncreased(int rank) {
		enableTalent(SEARING_LIGHT, rank);

		player.cast(HOLY_FIRE);

		updateUntil(30);

		assertDamageDone(HOLY_FIRE, (int) increaseByPct(481.5, 5 * rank) + increaseByPct(165, 5 * rank));
	}
}
