package wow.simulator.simulation.spell.tbc.ability.mage.frost;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.FROST_ARMOR;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class FrostArmorTest extends TbcMageSpellSimulationTest {
	/*
	Increases Armor by 200.
	If an enemy strikes the caster, they may have their movement slowed by 30% and the time between their attacks increased by 25% for 5 sec.
	Only one type of Armor spell can be active on the Mage at any time. Lasts 30 min.
	 */

	@Test
	void success() {
		player.cast(FROST_ARMOR);

		updateUntil(1800);

		assertEvents(
				at(0)
						.beginCast(player, FROST_ARMOR)
						.beginGcd(player)
						.endCast(player, FROST_ARMOR)
						.decreasedResource(170, MANA, player, FROST_ARMOR)
						.effectApplied(FROST_ARMOR, player, 1800),
				at(1.5)
						.endGcd(player),
				at(1800)
						.effectExpired(FROST_ARMOR, player)
		);
	}
}
