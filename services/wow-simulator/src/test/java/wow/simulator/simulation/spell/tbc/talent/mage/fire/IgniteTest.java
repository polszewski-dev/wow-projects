package wow.simulator.simulation.spell.tbc.talent.mage.fire;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.simulator.simulation.spell.tbc.talent.mage.TbcMageTalentSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.simulator.util.EffectType.TALENT;
import static wow.test.commons.AbilityNames.SCORCH;
import static wow.test.commons.TalentNames.IGNITE;

/**
 * User: POlszewski
 * Date: 2025-12-14
 */
class IgniteTest extends TbcMageTalentSimulationTest {
	/*
	Your critical strikes from Fire damage spells cause the target to burn for an additional 40% of your spell's damage over 4 sec.
	 */

	@ParameterizedTest
	@CsvSource({
			"1, 19, 20",
			"2, 39, 40",
			"3, 59, 60",
			"4, 79, 80",
			"5, 99, 100",
	})
	void effect_is_applied(int rank, int tick0Amount, int tick1Amount) {
		enableTalent(IGNITE, rank);

		critsOnlyOnFollowingRolls(0);

		player.cast(SCORCH);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SCORCH, 1.5)
						.beginGcd(player),
				at(1.5)
						.endCast(player, SCORCH)
						.decreasedResource(180, MANA, player, SCORCH)
						.decreasedResource(499, HEALTH, true, target, SCORCH)
						.effectApplied(IGNITE, TALENT, target, 4)
						.endGcd(player),
				at(3.5)
						.decreasedResource(tick0Amount, HEALTH, false, target, IGNITE),
				at(5.5)
						.decreasedResource(tick1Amount, HEALTH, false, target, IGNITE)
						.effectExpired(IGNITE, TALENT, target)
		);
	}

	@Test
	void effect_stacks_to_5() {
		enableTalent(IGNITE, 5);

		critsOnlyOnFollowingRolls(0, 1, 2, 3, 4);

		player.cast(SCORCH);
		player.cast(SCORCH);
		player.cast(SCORCH);
		player.cast(SCORCH);
		player.cast(SCORCH);

		updateUntil(30);

		assertEvents(
				testEvent -> testEvent.isEffect() || testEvent.isDamage(),
				at(1.5)
						.decreasedResource(499, HEALTH, true, target, SCORCH)
						.effectApplied(IGNITE, TALENT, target, 4),
				at(3)
						.decreasedResource(499, HEALTH, true, target, SCORCH)
						.effectStacked(IGNITE, TALENT, target, 2),
				at(3.5)
						.decreasedResource(199, HEALTH, target, IGNITE),
				at(4.5)
						.decreasedResource(499, HEALTH, true, target, SCORCH)
						.effectStacked(IGNITE, TALENT, target, 3),
				at(5.5)
						.decreasedResource(298, HEALTH, target, IGNITE),
				at(6)
						.decreasedResource(499, HEALTH, true, target, SCORCH)
						.effectStacked(IGNITE, TALENT, target, 4),
				at(7.5)
						.decreasedResource(398, HEALTH, target, IGNITE)
						.decreasedResource(499, HEALTH, true, target, SCORCH)
						.effectStacked(IGNITE, TALENT, target, 5),
				at(9.5)
						.decreasedResource(498, HEALTH, target, IGNITE),
				at(11.5)
						.decreasedResource(497, HEALTH, target, IGNITE)
						.effectExpired(IGNITE, TALENT, target)
		);
	}
}
