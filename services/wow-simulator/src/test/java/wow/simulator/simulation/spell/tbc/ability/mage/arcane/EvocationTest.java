package wow.simulator.simulation.spell.tbc.ability.mage.arcane;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.EVOCATION;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class EvocationTest extends TbcMageSpellSimulationTest {
	/*
	While channeling this spell, you gain 60% of your total mana over 8 sec.
	 */

	@Test
	void success() {
		player.setCurrentMana(0);
		player.cast(EVOCATION);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, EVOCATION)
						.beginGcd(player)
						.endCast(player, EVOCATION)
						.cooldownStarted(player, EVOCATION, 480)
						.effectApplied(EVOCATION, player, 8)
						.beginChannel(player, EVOCATION, 8),
				at(1.5)
						.endGcd(player),
				at(2)
						.increasedResource(629, MANA, player, EVOCATION),
				at(4)
						.increasedResource(629, MANA, player, EVOCATION),
				at(6)
						.increasedResource(629, MANA, player, EVOCATION),
				at(8)
						.increasedResource(630, MANA, player, EVOCATION)
						.effectExpired(EVOCATION, player)
						.endChannel(player, EVOCATION)
		);
	}
}
