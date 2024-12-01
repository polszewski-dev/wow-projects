package wow.simulator.simulation.spell.ability.warlock;

import org.junit.jupiter.api.Test;
import wow.commons.model.talent.TalentId;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SHADOWBURN;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class ShadowBurnTest extends SpellSimulationTest {
	@Test
	void success() {
		enableTalent(TalentId.SHADOWBURN, 1);

		player.cast(SHADOWBURN);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOWBURN)
						.endCast(player, SHADOWBURN)
						.decreasedResource(515, MANA, player, SHADOWBURN)
						.cooldownStarted(player, SHADOWBURN)
						.decreasedResource(631, HEALTH, target, SHADOWBURN)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(15)
						.cooldownExpired(player, SHADOWBURN)
		);
	}

	@Test
	void resisted() {
		rng.hitRoll = false;

		enableTalent(TalentId.SHADOWBURN, 1);

		player.cast(SHADOWBURN);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOWBURN)
						.endCast(player, SHADOWBURN)
						.decreasedResource(515, MANA, player, SHADOWBURN)
						.cooldownStarted(player, SHADOWBURN)
						.spellResisted(player, SHADOWBURN, target)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(15)
						.cooldownExpired(player, SHADOWBURN)
		);
	}

	@Test
	void interrupted() {
		enableTalent(TalentId.SHADOWBURN, 1);

		player.cast(SHADOWBURN);

		simulation.updateUntil(Time.at(1));

		player.interruptCurrentAction();

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOWBURN)
						.endCast(player, SHADOWBURN)
						.decreasedResource(515, MANA, player, SHADOWBURN)
						.cooldownStarted(player, SHADOWBURN)
						.decreasedResource(631, HEALTH, target, SHADOWBURN)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(15)
						.cooldownExpired(player, SHADOWBURN)
		);
	}
}
