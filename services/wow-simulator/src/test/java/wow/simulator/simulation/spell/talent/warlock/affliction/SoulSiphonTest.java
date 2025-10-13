package wow.simulator.simulation.spell.talent.warlock.affliction;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class SoulSiphonTest extends WarlockSpellSimulationTest {
	/*
	Increases the amount drained by your Drain Life spell by an additional 4% for each Affliction effect on the target, up to a maximum of 60% additional effect.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void damageIsIncreased(int rank) {
		enableTalent(TalentNames.SOUL_SIPHON, rank);

		player.cast(CURSE_OF_AGONY);
		player.cast(CORRUPTION);
		player.cast(DRAIN_LIFE);

		updateUntil(30);

		var numberOfEffects = 2;

		assertDamageDone(DRAIN_LIFE, DRAIN_LIFE_INFO.damage(), 2 * rank * numberOfEffects);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void healthGainedIsIncreased(int rank) {
		enableTalent(TalentNames.SOUL_SIPHON, rank);

		player.cast(CURSE_OF_AGONY);
		player.cast(CORRUPTION);
		player.cast(DRAIN_LIFE);

		updateUntil(30);

		var numberOfEffects = 2;

		assertHealthGained(DRAIN_LIFE, player, DRAIN_LIFE_INFO.damage(), 2 * rank * numberOfEffects);
	}

	@Override
	protected void afterSetUp() {
		setHealth(player, 1000);
	}
}
