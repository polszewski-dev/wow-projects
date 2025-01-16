package wow.simulator.simulation.spell.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.DRAIN_LIFE;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class DrainLifeTest extends WarlockSpellSimulationTest {
	@Test
	void success() {
		player.cast(DRAIN_LIFE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, DRAIN_LIFE, 5)
						.endCast(player, DRAIN_LIFE)
						.decreasedResource(425, MANA, player, DRAIN_LIFE)
						.beginChannel(player, DRAIN_LIFE)
						.effectApplied(DRAIN_LIFE, target, 5)
						.beginGcd(player),
				at(1)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE),
				at(3)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE),
				at(4)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE),
				at(5)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE)
						.effectExpired(DRAIN_LIFE, target)
						.endChannel(player, DRAIN_LIFE)
		);
	}

	@Test
	void resisted() {
		missesOnlyOnFollowingRolls(0);

		player.cast(DRAIN_LIFE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, DRAIN_LIFE, 5)
						.endCast(player, DRAIN_LIFE)
						.decreasedResource(425, MANA, player, DRAIN_LIFE)
						.spellResisted(player, DRAIN_LIFE, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player)
		);
	}

	@Test
	void interrupted() {
		player.cast(DRAIN_LIFE);

		simulation.updateUntil(Time.at(1.25));

		player.interruptCurrentAction();

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, DRAIN_LIFE, 5)
						.endCast(player, DRAIN_LIFE)
						.decreasedResource(425, MANA, player, DRAIN_LIFE)
						.beginChannel(player, DRAIN_LIFE)
						.effectApplied(DRAIN_LIFE, target, 5)
						.beginGcd(player),
				at(1)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE),
				at(1.25)
						.channelInterrupted(player, DRAIN_LIFE)
						.effectRemoved(DRAIN_LIFE, target)
						.endGcd(player)
		);
	}
}
