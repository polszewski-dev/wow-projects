package wow.simulator.simulation.spell.tbc.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.warlock.TbcWarlockTalentSimulationTest;

import static wow.test.commons.AbilityNames.*;
import static wow.test.commons.TalentNames.SOUL_SIPHON;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class SoulSiphonTest extends TbcWarlockTalentSimulationTest {
	/*
	Increases the amount drained by your Drain Life spell by an additional 4% for each Affliction effect on the target, up to a maximum of 60% additional effect.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void damage_is_increased(int rank) {
		player.cast(CURSE_OF_AGONY);
		player.cast(CORRUPTION);

		simulateTalent(SOUL_SIPHON, rank, DRAIN_LIFE);

		var numberOfEffects = 2;

		assertDamageIsIncreasedByPct(2 * rank * numberOfEffects);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void healing_is_increased(int rank) {
		player.cast(CURSE_OF_AGONY);
		player.cast(CORRUPTION);

		testingHealingAbility();

		simulateTalent(SOUL_SIPHON, rank, DRAIN_LIFE);

		var numberOfEffects = 2;

		assertSelfHealingIsIncreasedByPct(2 * rank * numberOfEffects);
	}

	@Override
	protected void afterSetUp() {
		setHealth(player, 1000);
	}
}
