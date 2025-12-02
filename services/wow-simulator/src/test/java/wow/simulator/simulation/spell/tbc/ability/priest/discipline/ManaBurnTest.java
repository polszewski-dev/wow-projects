package wow.simulator.simulation.spell.tbc.ability.priest.discipline;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.MANA_BURN;

/**
 * User: POlszewski
 * Date: 2025-12-02
 */
class ManaBurnTest extends TbcPriestSpellSimulationTest {
	/*
	Destroy 1021 to 1079 mana from a target. For each mana destroyed in this way, the target takes 0.5 Shadow damage.
	 */

	@Test
	void success() {
		player.cast(MANA_BURN);

		updateUntil(30);

		assertEvents(
			at(0)
					.beginCast(player, MANA_BURN, 3)
					.beginGcd(player),
			at(1.5)
					.endGcd(player),
			at(3)
					.endCast(player, MANA_BURN)
					.decreasedResource(355, MANA, player, MANA_BURN)
					.decreasedResource(1050, MANA, target, MANA_BURN)
					.decreasedResource(525, HEALTH, target, MANA_BURN)
		);
	}

	@Override
	protected void afterSetUp() {
		target.addHiddenEffect("Bonus Intellect", 1000);
		target.setManaToMax();
	}
}
