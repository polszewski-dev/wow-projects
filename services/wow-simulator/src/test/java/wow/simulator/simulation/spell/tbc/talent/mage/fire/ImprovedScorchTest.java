package wow.simulator.simulation.spell.tbc.talent.mage.fire;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;
import wow.simulator.util.TestEvent;

import static wow.simulator.util.EffectType.TALENT;
import static wow.test.commons.AbilityNames.SCORCH;
import static wow.test.commons.TalentNames.IMPROVED_SCORCH;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class ImprovedScorchTest extends TbcMageTalentSimulationTest {
	/*
	Your Scorch spells have a 100% chance to cause your target to be vulnerable to Fire damage.
	This vulnerability increases the Fire damage dealt to your target by 3% and lasts 30 sec.
	Stacks up to 5 times.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3 })
	void effect_is_applied(int rank) {
		enableTalent(IMPROVED_SCORCH, rank);

		eventsOnlyOnFollowingRolls(0);

		player.cast(SCORCH);

		updateUntil(60);

		assertEvents(
				testEvent -> testEvent.isCast() || testEvent.isEffect(),
				at(0)
						.beginCast(player, SCORCH, 1.5),
				at(1.5)
						.endCast(player, SCORCH)
						.effectApplied(IMPROVED_SCORCH, TALENT, target, 30),
				at(31.5)
						.effectExpired(IMPROVED_SCORCH, TALENT, target)
		);
	}

	@ParameterizedTest
	@CsvSource({
			"1, 33",
			"2, 66",
			"3, 100"
	})
	void correct_event_chance(int rank, int expected) {
		enableTalent(IMPROVED_SCORCH, rank);

		eventsOnlyOnFollowingRolls(0);

		player.cast(SCORCH);

		updateUntil(60);

		assertLastEventChance(expected);
	}

	@Test
	void effect_stacks_to_5() {
		enableTalent(IMPROVED_SCORCH, 3);

		eventsOnlyOnFollowingRolls(0, 1, 2, 3, 4, 5);

		player.cast(SCORCH);
		player.cast(SCORCH);
		player.cast(SCORCH);
		player.cast(SCORCH);
		player.cast(SCORCH);
		player.cast(SCORCH);

		updateUntil(60);

		assertEvents(
				TestEvent::isEffect,
				at(1.5)
						.effectApplied(IMPROVED_SCORCH, TALENT, target, 30),
				at(3)
						.effectStacked(IMPROVED_SCORCH, TALENT, target, 2),
				at(4.5)
						.effectStacked(IMPROVED_SCORCH, TALENT, target, 3),
				at(6)
						.effectStacked(IMPROVED_SCORCH, TALENT, target, 4),
				at(7.5)
						.effectStacked(IMPROVED_SCORCH, TALENT, target, 5),
				at(9)
						.effectStacked(IMPROVED_SCORCH, TALENT, target, 5),
				at(39)
						.effectExpired(IMPROVED_SCORCH, TALENT, target)
		);
	}
}
