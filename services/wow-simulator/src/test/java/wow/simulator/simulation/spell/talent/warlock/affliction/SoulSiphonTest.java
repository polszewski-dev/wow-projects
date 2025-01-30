package wow.simulator.simulation.spell.talent.warlock.affliction;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.DRAIN_LIFE;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
@Disabled
class SoulSiphonTest extends WarlockSpellSimulationTest {
	/*
	Increases the amount drained by your Drain Life spell by an additional 4% for each Affliction effect on the target, up to a maximum of 60% additional effect.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void damageIsIncreased(int rank) {
		enableTalent(TalentId.SOUL_SIPHON, rank);

		player.cast(DRAIN_LIFE);

		updateUntil(30);

		assertDamageDone(DRAIN_LIFE, DRAIN_LIFE_INFO.damage(), 2 * rank);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2 })
	void healthGainedIsIncreased(int rank) {
		enableTalent(TalentId.SOUL_SIPHON, rank);

		player.cast(DRAIN_LIFE);

		updateUntil(30);

		assertHealthGained(DRAIN_LIFE, player, DRAIN_LIFE_INFO.damage(), 2 * rank);
	}
}
