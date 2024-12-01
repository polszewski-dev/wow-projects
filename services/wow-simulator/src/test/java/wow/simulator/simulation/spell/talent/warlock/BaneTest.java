package wow.simulator.simulation.spell.talent.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.SpellSimulationTest;

import static wow.commons.model.spell.AbilityId.IMMOLATE;
import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.commons.model.talent.TalentId.BANE;

/**
 * User: POlszewski
 * Date: 2024-12-01
 */
class BaneTest extends SpellSimulationTest {
	@Test
	void shadowBolt() {
		enableTalent(BANE, 5);

		player.cast(SHADOW_BOLT);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2.5)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.decreasedResource(575, HEALTH, target, SHADOW_BOLT)
		);
	}

	@Test
	void immolate() {
		enableTalent(BANE, 5);

		player.cast(IMMOLATE);

		simulation.updateUntil(Time.at(30));

		assertEvents(
				at(0)
						.beginCast(player, IMMOLATE)
						.beginGcd(player),
				at(1.5)
						.endCast(player, IMMOLATE)
						.decreasedResource(445, MANA, player, IMMOLATE)
						.decreasedResource(332, HEALTH, target, IMMOLATE)
						.effectApplied(IMMOLATE, target)
						.endGcd(player),
				at(4.5)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(7.5)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(10.5)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(13.5)
						.decreasedResource(123, HEALTH, target, IMMOLATE),
				at(16.5)
						.decreasedResource(123, HEALTH, target, IMMOLATE)
						.effectExpired(IMMOLATE, target)
		);
	}
}
