package wow.simulator.simulation.spell.ability.warlock;

import org.junit.jupiter.api.Test;
import wow.commons.model.talent.TalentId;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SIPHON_LIFE;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class SiphonLifeTest extends SpellSimulationTest {
	@Test
	void success() {
		enableTalent(TalentId.SIPHON_LIFE, 1);

		player.cast(SIPHON_LIFE);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SIPHON_LIFE)
						.endCast(player, SIPHON_LIFE)
						.decreasedResource(410, MANA, player, SIPHON_LIFE)
						.effectApplied(SIPHON_LIFE, target)
						.beginGcd(player),
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
		rng.hitRoll = false;

		enableTalent(TalentId.SIPHON_LIFE, 1);

		player.cast(SIPHON_LIFE);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SIPHON_LIFE)
						.endCast(player, SIPHON_LIFE)
						.decreasedResource(410, MANA, player, SIPHON_LIFE)
						.spellResisted(player, SIPHON_LIFE, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player)
		);
	}

	@Test
	void interrupted() {
		enableTalent(TalentId.SIPHON_LIFE, 1);

		player.cast(SIPHON_LIFE);

		simulation.updateUntil(Time.at(1));

		player.interruptCurrentAction();

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SIPHON_LIFE)
						.endCast(player, SIPHON_LIFE)
						.decreasedResource(410, MANA, player, SIPHON_LIFE)
						.effectApplied(SIPHON_LIFE, target)
						.beginGcd(player),
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
}
