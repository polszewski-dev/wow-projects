package wow.simulator.simulation.spell.tbc.ability.mage.arcane;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.ARCANE_INTELLECT;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class ArcaneIntellectTest extends TbcMageSpellSimulationTest {
	/*
	Increases the target's Intellect by 40 for 30 min.
	 */

	@Test
	void success() {
		player.cast(ARCANE_INTELLECT);

		updateUntil(1800);

		assertEvents(
				at(0)
						.beginCast(player, ARCANE_INTELLECT)
						.beginGcd(player)
						.endCast(player, ARCANE_INTELLECT)
						.decreasedResource(700, MANA, player, ARCANE_INTELLECT)
						.effectApplied(ARCANE_INTELLECT, player, 1800),
				at(1.5)
						.endGcd(player),
				at(1800)
						.effectExpired(ARCANE_INTELLECT, player)
		);
	}

	@Test
	void intellect_is_increased() {
		simulateBuffSpell(ARCANE_INTELLECT);

		assertIntellectIncreasedBy(40);
	}
}
