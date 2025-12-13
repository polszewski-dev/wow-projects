package wow.simulator.simulation.spell.tbc.talent.mage.frost;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;
import wow.simulator.util.TestEvent;

import static wow.simulator.util.EffectType.TALENT;
import static wow.test.commons.AbilityNames.FROSTBOLT;
import static wow.test.commons.TalentNames.WINTERS_CHILL;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class WintersChillTest extends TbcMageTalentSimulationTest {
	/*
	Gives your Frost damage spells a 100% chance to apply the Winter's Chill effect,
	which increases the chance a Frost spell will critically hit the target by 2% for 15 sec.
	Stacks up to 5 times.
	 */

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void effect_is_applied(int rank) {
		enableTalent(WINTERS_CHILL, rank);

		eventsOnlyOnFollowingRolls(0);

		player.cast(FROSTBOLT);

		updateUntil(60);

		assertEvents(
				testEvent -> testEvent.isCast() || testEvent.isEffect(),
				at(0)
						.beginCast(player, FROSTBOLT, 3),
				at(3)
						.endCast(player, FROSTBOLT)
						.effectApplied(WINTERS_CHILL, TALENT, target, 15),
				at(18)
						.effectExpired(WINTERS_CHILL, TALENT, target)

		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 2, 3, 4, 5 })
	void correct_event_chance(int rank) {
		enableTalent(WINTERS_CHILL, rank);

		eventsOnlyOnFollowingRolls(0);

		player.cast(FROSTBOLT);

		updateUntil(60);

		assertLastEventChance(20 * rank);
	}

	@Test
	void effect_stacks_to_5() {
		enableTalent(WINTERS_CHILL, 5);

		eventsOnlyOnFollowingRolls(0, 1, 2, 3, 4, 5);

		player.cast(FROSTBOLT);
		player.cast(FROSTBOLT);
		player.cast(FROSTBOLT);
		player.cast(FROSTBOLT);
		player.cast(FROSTBOLT);
		player.cast(FROSTBOLT);

		updateUntil(60);

		assertEvents(
				TestEvent::isEffect,
				at(3)
						.effectApplied(WINTERS_CHILL, TALENT, target, 15),
				at(6)
						.effectStacked(WINTERS_CHILL, TALENT, target, 2),
				at(9)
						.effectStacked(WINTERS_CHILL, TALENT, target, 3),
				at(12)
						.effectStacked(WINTERS_CHILL, TALENT, target, 4),
				at(15)
						.effectStacked(WINTERS_CHILL, TALENT, target, 5),
				at(18)
						.effectStacked(WINTERS_CHILL, TALENT, target, 5),
				at(33)
						.effectExpired(WINTERS_CHILL, TALENT, target)
		);
	}
}
