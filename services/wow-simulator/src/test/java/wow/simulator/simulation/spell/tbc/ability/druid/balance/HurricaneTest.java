package wow.simulator.simulation.spell.tbc.ability.druid.balance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcDruidSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.HURRICANE;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class HurricaneTest extends TbcDruidSpellSimulationTest {
	/*
	Creates a violent storm in the target area causing 206 Nature damage to enemies every 1 sec,
	and increasing the time between attacks by 25%. Lasts 10 sec. Druid must channel to maintain the spell.
	 */

	@Test
	void success() {
		player.cast(HURRICANE);

		updateUntil(60);

		assertEvents(
				at(0)
						.beginCast(player, HURRICANE)
						.beginGcd(player)
						.endCast(player, HURRICANE)
						.decreasedResource(1905, MANA, player, HURRICANE)
						.cooldownStarted(player, HURRICANE, 60)
						.effectApplied(HURRICANE, null, 10)
						.beginChannel(player, HURRICANE, 10),
				at(1)
						.decreasedResource(206, HEALTH, target, HURRICANE)
						.decreasedResource(206, HEALTH, target2, HURRICANE)
						.decreasedResource(206, HEALTH, target3, HURRICANE)
						.decreasedResource(206, HEALTH, target4, HURRICANE),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(206, HEALTH, target, HURRICANE)
						.decreasedResource(206, HEALTH, target2, HURRICANE)
						.decreasedResource(206, HEALTH, target3, HURRICANE)
						.decreasedResource(206, HEALTH, target4, HURRICANE),
				at(3)
						.decreasedResource(206, HEALTH, target, HURRICANE)
						.decreasedResource(206, HEALTH, target2, HURRICANE)
						.decreasedResource(206, HEALTH, target3, HURRICANE)
						.decreasedResource(206, HEALTH, target4, HURRICANE),
				at(4)
						.decreasedResource(206, HEALTH, target, HURRICANE)
						.decreasedResource(206, HEALTH, target2, HURRICANE)
						.decreasedResource(206, HEALTH, target3, HURRICANE)
						.decreasedResource(206, HEALTH, target4, HURRICANE),
				at(5)
						.decreasedResource(206, HEALTH, target, HURRICANE)
						.decreasedResource(206, HEALTH, target2, HURRICANE)
						.decreasedResource(206, HEALTH, target3, HURRICANE)
						.decreasedResource(206, HEALTH, target4, HURRICANE),
				at(6)
						.decreasedResource(206, HEALTH, target, HURRICANE)
						.decreasedResource(206, HEALTH, target2, HURRICANE)
						.decreasedResource(206, HEALTH, target3, HURRICANE)
						.decreasedResource(206, HEALTH, target4, HURRICANE),
				at(7)
						.decreasedResource(206, HEALTH, target, HURRICANE)
						.decreasedResource(206, HEALTH, target2, HURRICANE)
						.decreasedResource(206, HEALTH, target3, HURRICANE)
						.decreasedResource(206, HEALTH, target4, HURRICANE),
				at(8)
						.decreasedResource(206, HEALTH, target, HURRICANE)
						.decreasedResource(206, HEALTH, target2, HURRICANE)
						.decreasedResource(206, HEALTH, target3, HURRICANE)
						.decreasedResource(206, HEALTH, target4, HURRICANE),
				at(9)
						.decreasedResource(206, HEALTH, target, HURRICANE)
						.decreasedResource(206, HEALTH, target2, HURRICANE)
						.decreasedResource(206, HEALTH, target3, HURRICANE)
						.decreasedResource(206, HEALTH, target4, HURRICANE),
				at(10)
						.decreasedResource(206, HEALTH, target, HURRICANE)
						.decreasedResource(206, HEALTH, target2, HURRICANE)
						.decreasedResource(206, HEALTH, target3, HURRICANE)
						.decreasedResource(206, HEALTH, target4, HURRICANE)
						.effectExpired(HURRICANE, null)
						.endChannel(player, HURRICANE),
				at(60)
						.cooldownExpired(player, HURRICANE)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(HURRICANE, spellDamage);

		assertDamageDone(HURRICANE_INFO, target, spellDamage);
		assertDamageDone(HURRICANE_INFO, target2, spellDamage);
		assertDamageDone(HURRICANE_INFO, target3, spellDamage);
		assertDamageDone(HURRICANE_INFO, target4, spellDamage);
	}
}
