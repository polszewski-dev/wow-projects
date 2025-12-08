package wow.simulator.simulation.spell.tbc.ability.paladin.holy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcPaladinSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.CONSECRATION;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class ConsecrationTest extends TbcPaladinSpellSimulationTest {
	/*
	Consecrates the land beneath the Paladin, doing 512 Holy damage over 8 sec to enemies who enter the area.
	 */

	@Test
	void success() {
		player.cast(CONSECRATION);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CONSECRATION)
						.beginGcd(player)
						.endCast(player, CONSECRATION)
						.decreasedResource(660, MANA, player, CONSECRATION)
						.cooldownStarted(player, CONSECRATION, 8)
						.effectApplied(CONSECRATION, null, 8),
				at(1)
						.decreasedResource(64, HEALTH, target, CONSECRATION)
						.decreasedResource(64, HEALTH, target2, CONSECRATION)
						.decreasedResource(64, HEALTH, target3, CONSECRATION)
						.decreasedResource(64, HEALTH, target4, CONSECRATION),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(64, HEALTH, target, CONSECRATION)
						.decreasedResource(64, HEALTH, target2, CONSECRATION)
						.decreasedResource(64, HEALTH, target3, CONSECRATION)
						.decreasedResource(64, HEALTH, target4, CONSECRATION),
				at(3)
						.decreasedResource(64, HEALTH, target, CONSECRATION)
						.decreasedResource(64, HEALTH, target2, CONSECRATION)
						.decreasedResource(64, HEALTH, target3, CONSECRATION)
						.decreasedResource(64, HEALTH, target4, CONSECRATION),
				at(4)
						.decreasedResource(64, HEALTH, target, CONSECRATION)
						.decreasedResource(64, HEALTH, target2, CONSECRATION)
						.decreasedResource(64, HEALTH, target3, CONSECRATION)
						.decreasedResource(64, HEALTH, target4, CONSECRATION),
				at(5)
						.decreasedResource(64, HEALTH, target, CONSECRATION)
						.decreasedResource(64, HEALTH, target2, CONSECRATION)
						.decreasedResource(64, HEALTH, target3, CONSECRATION)
						.decreasedResource(64, HEALTH, target4, CONSECRATION),
				at(6)
						.decreasedResource(64, HEALTH, target, CONSECRATION)
						.decreasedResource(64, HEALTH, target2, CONSECRATION)
						.decreasedResource(64, HEALTH, target3, CONSECRATION)
						.decreasedResource(64, HEALTH, target4, CONSECRATION),
				at(7)
						.decreasedResource(64, HEALTH, target, CONSECRATION)
						.decreasedResource(64, HEALTH, target2, CONSECRATION)
						.decreasedResource(64, HEALTH, target3, CONSECRATION)
						.decreasedResource(64, HEALTH, target4, CONSECRATION),
				at(8)
						.cooldownExpired(player, CONSECRATION)
						.decreasedResource(64, HEALTH, target, CONSECRATION)
						.decreasedResource(64, HEALTH, target2, CONSECRATION)
						.decreasedResource(64, HEALTH, target3, CONSECRATION)
						.decreasedResource(64, HEALTH, target4, CONSECRATION)
						.effectExpired(CONSECRATION, null)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(CONSECRATION, spellDamage);

		assertDamageDone(CONSECRATION_INFO, target, spellDamage);
		assertDamageDone(CONSECRATION_INFO, target2, spellDamage);
		assertDamageDone(CONSECRATION_INFO, target3, spellDamage);
		assertDamageDone(CONSECRATION_INFO, target4, spellDamage);
	}
}
