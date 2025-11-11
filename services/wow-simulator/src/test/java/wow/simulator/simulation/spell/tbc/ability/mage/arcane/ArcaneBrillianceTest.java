package wow.simulator.simulation.spell.tbc.ability.mage.arcane;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.ARCANE_BRILLIANCE;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class ArcaneBrillianceTest extends TbcMageSpellSimulationTest {
	/*
	Infuses the target's party with brilliance, increasing their Intellect by 40 for 1 hour.
	 */

	@Test
	void success() {
		player.cast(ARCANE_BRILLIANCE, player2);

		updateUntil(3600);

		assertEvents(
				at(0)
						.beginCast(player, ARCANE_BRILLIANCE)
						.beginGcd(player)
						.endCast(player, ARCANE_BRILLIANCE)
						.decreasedResource(1800, MANA, player, ARCANE_BRILLIANCE)
						.effectApplied(ARCANE_BRILLIANCE, player2, 3600)
						.effectApplied(ARCANE_BRILLIANCE, player3, 3600)
						.effectApplied(ARCANE_BRILLIANCE, player4, 3600),
				at(1.5)
						.endGcd(player),
				at(3600)
						.effectExpired(ARCANE_BRILLIANCE, player2)
						.effectExpired(ARCANE_BRILLIANCE, player3)
						.effectExpired(ARCANE_BRILLIANCE, player4)
		);
	}

	@Test
	void intellect_is_increased() {
		simulateBuffSpell(ARCANE_BRILLIANCE);

		assertIntellectIncreasedBy(40);
	}

	@Override
	protected void afterSetUp() {
		player2.getParty().add(player3, player4);
	}
}
