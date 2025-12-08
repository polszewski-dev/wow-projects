package wow.simulator.simulation.spell.tbc.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.DRAIN_LIFE;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class DrainLifeTest extends TbcWarlockSpellSimulationTest {
	/*
	Transfers 108 health every 1 sec from the target to the caster.  Lasts 5 sec.
	 */

	@Test
	void success() {
		player.cast(DRAIN_LIFE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, DRAIN_LIFE)
						.beginGcd(player)
						.endCast(player, DRAIN_LIFE)
						.decreasedResource(425, MANA, player, DRAIN_LIFE)
						.effectApplied(DRAIN_LIFE, target, 5)
						.beginChannel(player, DRAIN_LIFE, 5),
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
						.beginCast(player, DRAIN_LIFE)
						.beginGcd(player)
						.endCast(player, DRAIN_LIFE)
						.decreasedResource(425, MANA, player, DRAIN_LIFE)
						.spellResisted(player, DRAIN_LIFE, target),
				at(1.5)
						.endGcd(player)
		);
	}

	@Test
	void interrupted() {
		player.cast(DRAIN_LIFE);

		runAt(1.25, player::interruptCurrentAction);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, DRAIN_LIFE)
						.beginGcd(player)
						.endCast(player, DRAIN_LIFE)
						.decreasedResource(425, MANA, player, DRAIN_LIFE)
						.effectApplied(DRAIN_LIFE, target, 5)
						.beginChannel(player, DRAIN_LIFE, 5),
				at(1)
						.decreasedResource(108, HEALTH, target, DRAIN_LIFE)
						.increasedResource(108, HEALTH, player, DRAIN_LIFE),
				at(1.25)
						.channelInterrupted(player, DRAIN_LIFE)
						.effectRemoved(DRAIN_LIFE, target),
				at(1.5)
						.endGcd(player)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(DRAIN_LIFE, spellDamage);

		assertDamageDone(DRAIN_LIFE_INFO, spellDamage);
	}

	@Test
	void healthGained() {
		player.cast(DRAIN_LIFE);

		updateUntil(30);

		assertHealthGained(DRAIN_LIFE, player, DRAIN_LIFE_INFO.damage());
	}

	@Override
	protected void afterSetUp() {
		setHealth(player, 1000);
	}
}
