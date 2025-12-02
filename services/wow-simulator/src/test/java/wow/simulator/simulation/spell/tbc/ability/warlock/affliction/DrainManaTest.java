package wow.simulator.simulation.spell.tbc.ability.warlock.affliction;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.DRAIN_MANA;

/**
 * User: POlszewski
 * Date: 2025-12-02
 */
class DrainManaTest extends TbcWarlockSpellSimulationTest {
	/*
	Transfers 200 Mana every 1 sec from the target to the caster. Lasts 5 sec.
	 */

	@Test
	void success() {
		player.cast(DRAIN_MANA);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, DRAIN_MANA)
						.beginGcd(player)
						.endCast(player, DRAIN_MANA)
						.decreasedResource(455, MANA, player, DRAIN_MANA)
						.effectApplied(DRAIN_MANA, target, 5)
						.beginChannel(player, DRAIN_MANA, 5),
				at(1)
						.decreasedResource(200, MANA, target, DRAIN_MANA)
						.increasedResource(200, MANA, player, DRAIN_MANA),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(200, MANA, target, DRAIN_MANA)
						.increasedResource(200, MANA, player, DRAIN_MANA),
				at(3)
						.decreasedResource(200, MANA, target, DRAIN_MANA)
						.increasedResource(200, MANA, player, DRAIN_MANA),
				at(4)
						.decreasedResource(200, MANA, target, DRAIN_MANA)
						.increasedResource(200, MANA, player, DRAIN_MANA),
				at(5)
						.decreasedResource(200, MANA, target, DRAIN_MANA)
						.increasedResource(200, MANA, player, DRAIN_MANA)
						.effectExpired(DRAIN_MANA, target)
						.endChannel(player, DRAIN_MANA)
		);
	}

	@Override
	protected void afterSetUp() {
		setMana(player, 1000);
		target.addHiddenEffect("Bonus Intellect", 1000);
		target.setManaToMax();
	}
}
