package wow.simulator.simulation.spell.talent.priest.holy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.test.commons.AbilityNames.HOLY_FIRE;
import static wow.test.commons.AbilityNames.SMITE;
import static wow.test.commons.TalentNames.DIVINE_FURY;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class DivineFuryTest extends PriestSpellSimulationTest {
	/*
	Reduces the casting time of your Smite, Holy Fire, Heal and Greater Heal spells by 0.5 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void smiteCastTimeIsReduced(int rank) {
		enableTalent(DIVINE_FURY, rank);

		player.cast(SMITE);

		updateUntil(30);

		assertCastTime(SMITE, SMITE_INFO.baseCastTime() - 0.1 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void holyFireCastTimeIsReduced(int rank) {
		enableTalent(DIVINE_FURY, rank);

		player.cast(HOLY_FIRE);

		updateUntil(30);

		assertCastTime(HOLY_FIRE, HOLY_FIRE_INFO.baseCastTime() - 0.1 * rank);
	}
}
