package wow.simulator.simulation.spell.tbc.ability.mage.frost;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.ICE_ARMOR;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class IceArmorTest extends TbcMageSpellSimulationTest {
	/*
	Increases Armor by 645 and frost resistance by 18.
	If an enemy strikes the caster, they may have their movement slowed by 30% and the time between their attacks increased by 25% for 5 sec.
	Only one type of Armor spell can be active on the Mage at any time. Lasts 30 min.
	 */

	@Test
	void success() {
		player.cast(ICE_ARMOR);

		updateUntil(1800);

		assertEvents(
				at(0)
						.beginCast(player, ICE_ARMOR)
						.beginGcd(player)
						.endCast(player, ICE_ARMOR)
						.decreasedResource(630, MANA, player, ICE_ARMOR)
						.effectApplied(ICE_ARMOR, player, 1800),
				at(1.5)
						.endGcd(player),
				at(1800)
						.effectExpired(ICE_ARMOR, player)
		);
	}
}
