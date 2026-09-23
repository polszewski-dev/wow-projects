package wow.simulator.simulation;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.ResourceType;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.test.commons.AbilityNames.CORRUPTION;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2026-08-15
 */
class DeathTest extends TbcWarlockSpellSimulationTest {
	@Test
	void target_died_event_is_triggered() {
		target.setCurrentHealth(1);
		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertEvents(
			at(0)
					.beginCast(player, SHADOW_BOLT, 3)
					.beginGcd(player),
			at(1.5)
					.endGcd(player),
			at(3)
					.endCast(player, SHADOW_BOLT)
					.decreasedResource(420, ResourceType.MANA, player, SHADOW_BOLT)
					.decreasedResource(1, ResourceType.HEALTH, target, SHADOW_BOLT)
					.targetDied(target, player)
		);
	}

	@Test
	void invalid_target_error_on_second_cast() {
		target.setCurrentHealth(1);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT, 3)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, ResourceType.MANA, player, SHADOW_BOLT)
						.decreasedResource(1, ResourceType.HEALTH, target, SHADOW_BOLT)
						.targetDied(target, player)
						.canNotBeCasted(player, SHADOW_BOLT)
		);
	}

	@Test
	void dot_is_removed() {
		target.setCurrentHealth(1);
		player.cast(CORRUPTION);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION, 2)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, ResourceType.MANA, player, CORRUPTION)
						.effectApplied(CORRUPTION, target, 18),
				at(5)
						.decreasedResource(1, ResourceType.HEALTH, target, CORRUPTION)
						.effectRemoved(CORRUPTION, target)
						.targetDied(target, player)
		);
	}

	@Test
	void simulation_stops_on_targets_death() {
		target.setCurrentHealth(1);
		player.cast(CORRUPTION);
		player.cast(SHADOW_BOLT, target2);

		target.setOnDeath(x -> simulation.finish());

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CORRUPTION, 2)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, CORRUPTION)
						.decreasedResource(370, ResourceType.MANA, player, CORRUPTION)
						.effectApplied(CORRUPTION, target, 18)
						.beginCast(player, SHADOW_BOLT, 3)
						.beginGcd(player),
				at(3.5)
						.endGcd(player),
				at(5)
						.decreasedResource(1, ResourceType.HEALTH, target, CORRUPTION)
						.effectRemoved(CORRUPTION, target)
						.castInterrupted(player, SHADOW_BOLT)
						.targetDied(target, player)
		);
	}
}
