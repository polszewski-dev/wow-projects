package wow.simulator.simulation.spell.tbc.ability.priest.holy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.RENEW;

/**
 * User: POlszewski
 * Date: 2025-11-16
 */
class RenewTest extends TbcPriestSpellSimulationTest {
	/*
	Heals the target for 1110 over 15 sec.
	 */

	@Test
	void success() {
		player2.setCurrentHealth(1);

		player.cast(RENEW, player2);

		updateUntil(30);

		assertEvents(
			at(0)
					.beginCast(player, RENEW)
					.beginGcd(player)
					.endCast(player, RENEW)
					.decreasedResource(450, MANA, player, RENEW)
					.effectApplied(RENEW, player2, 15),
			at(1.5)
					.endGcd(player),
			at(3)
					.increasedResource(222, HEALTH, player2, RENEW),
			at(6)
					.increasedResource(222, HEALTH, player2, RENEW),
			at(9)
					.increasedResource(222, HEALTH, player2, RENEW),
			at(12)
					.increasedResource(222, HEALTH, player2, RENEW),
			at(15)
					.increasedResource(222, HEALTH, player2, RENEW)
					.effectExpired(RENEW, player2)
		);
	}

	@ParameterizedTest
	@MethodSource("spellHealingLevels")
	void healing_done(int healing) {
		simulateHealingSpell(RENEW, healing);

		assertHealthGained(RENEW_INFO, healing);
	}
}
