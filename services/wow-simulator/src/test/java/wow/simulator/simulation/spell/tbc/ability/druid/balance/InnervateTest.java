package wow.simulator.simulation.spell.tbc.ability.druid.balance;

import org.junit.jupiter.api.Test;
import wow.simulator.model.time.Time;
import wow.simulator.simulation.spell.tbc.TbcDruidSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.INNERVATE;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class InnervateTest extends TbcDruidSpellSimulationTest {
	/*
	Increases the target's Spirit based mana regeneration by 400% and allows full mana regeneration while casting. Lasts 20 sec.
	 */

	@Test
	void success() {
		player.setCurrentMana(200);

		player.cast(INNERVATE);

		updateUntil(360);

		assertEvents(
				testEvent -> !testEvent.isManaGained() || testEvent.time().before(Time.at(25)),
				at(0)
						.beginCast(player, INNERVATE)
						.beginGcd(player)
						.endCast(player, INNERVATE)
						.decreasedResource(154, MANA, player, INNERVATE)
						.cooldownStarted(player, INNERVATE, 360)
						.effectApplied(INNERVATE, player, 20),
				at(1.5)
						.endGcd(player),
				at(2)
						.increasedResource(136, MANA, player, null),
				at(4)
						.increasedResource(136, MANA, player, null),
				at(6)
						.increasedResource(136, MANA, player, null),
				at(8)
						.increasedResource(136, MANA, player, null),
				at(10)
						.increasedResource(136, MANA, player, null),
				at(12)
						.increasedResource(136, MANA, player, null),
				at(14)
						.increasedResource(136, MANA, player, null),
				at(16)
						.increasedResource(136, MANA, player, null),
				at(18)
						.increasedResource(136, MANA, player, null),
				at(20)
						.effectExpired(INNERVATE, player)
						.increasedResource(136, MANA, player, null),
				at(22)
						.increasedResource(27, MANA, player, null),
				at(24)
						.increasedResource(27, MANA, player, null),
				at(360)
						.cooldownExpired(player, INNERVATE)
		);
	}

	@Override
	protected void afterSetUp() {
		handler.setIgnoreRegen(false);
	}
}
