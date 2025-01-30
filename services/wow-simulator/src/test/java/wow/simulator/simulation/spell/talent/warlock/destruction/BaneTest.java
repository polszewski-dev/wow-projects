package wow.simulator.simulation.spell.talent.warlock.destruction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.IMMOLATE;
import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.talent.TalentId.BANE;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class BaneTest extends WarlockSpellSimulationTest {
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
