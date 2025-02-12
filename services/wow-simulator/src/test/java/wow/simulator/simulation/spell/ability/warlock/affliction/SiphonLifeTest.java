package wow.simulator.simulation.spell.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.commons.model.talent.TalentId;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SIPHON_LIFE;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class SiphonLifeTest extends WarlockSpellSimulationTest {
	/*
	Transfers 63 health from the target to the caster every 3 sec.  Lasts 30 sec.
	 */

	@Test
	void success() {
		player.cast(SIPHON_LIFE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SIPHON_LIFE)
						.beginGcd(player)
						.endCast(player, SIPHON_LIFE)
						.decreasedResource(410, MANA, player, SIPHON_LIFE)
						.effectApplied(SIPHON_LIFE, target, 30),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(6)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(9)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(12)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(15)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(18)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(21)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(24)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(27)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(30)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE)
						.effectExpired(SIPHON_LIFE, target)
		);
	}

	@Test
	void resisted() {
		missesOnlyOnFollowingRolls(0);

		player.cast(SIPHON_LIFE);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SIPHON_LIFE)
						.beginGcd(player)
						.endCast(player, SIPHON_LIFE)
						.decreasedResource(410, MANA, player, SIPHON_LIFE)
						.spellResisted(player, SIPHON_LIFE, target),
				at(1.5)
						.endGcd(player)
		);
	}

	@Test
	void interrupted() {
		player.cast(SIPHON_LIFE);

		updateUntil(1);

		player.interruptCurrentAction();

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SIPHON_LIFE)
						.beginGcd(player)
						.endCast(player, SIPHON_LIFE)
						.decreasedResource(410, MANA, player, SIPHON_LIFE)
						.effectApplied(SIPHON_LIFE, target, 30),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(6)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(9)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(12)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(15)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(18)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(21)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(24)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(27)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE),
				at(30)
						.decreasedResource(63, HEALTH, target, SIPHON_LIFE)
						.increasedResource(63, HEALTH, player, SIPHON_LIFE)
						.effectExpired(SIPHON_LIFE, target)
		);
	}

	@Test
	void damageDone() {
		player.cast(SIPHON_LIFE);

		updateUntil(30);

		assertDamageDone(SIPHON_LIFE, SIPHON_LIFE_INFO.damage());
	}

	@Test
	void healthGained() {
		player.cast(SIPHON_LIFE);

		updateUntil(30);

		assertHealthGained(SIPHON_LIFE, player, SIPHON_LIFE_INFO.damage());
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentId.SIPHON_LIFE, 1);
	}
}
