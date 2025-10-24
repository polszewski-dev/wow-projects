package wow.simulator.simulation.spell.tbc.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.IMMOLATE;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;
import static wow.test.commons.TalentNames.BANE;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class BaneTest extends TbcWarlockSpellSimulationTest {
	/*
	Reduces the casting time of your Shadow Bolt and Immolate spells by 0.5 sec and your Soul Fire spell by 2 sec.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void shadowBolt(int rank) {
		enableTalent(BANE, rank);

		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertCastTime(SHADOW_BOLT, SHADOW_BOLT_INFO.baseCastTime() - 0.1 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void immolate(int rank) {
		enableTalent(BANE, rank);

		player.cast(IMMOLATE);

		updateUntil(30);

		assertCastTime(IMMOLATE, IMMOLATE_INFO.baseCastTime() - 0.1 * rank);
	}
}
