package wow.simulator.simulation.spell.tbc.ability.shaman.enhancement;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcShamanSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.WRATH_OF_AIR_TOTEM;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class WrathOfAirTotemTest extends TbcShamanSpellSimulationTest {
	/*
	Summons a Wrath of Air Totem with 5 health at the feet of the caster.
	Party members within 20 yards of the totem have their spell damage and healing increased by up to 101. Lasts 2 min.
	 */

	@Test
	void success() {
		player.cast(WRATH_OF_AIR_TOTEM);

		updateUntil(120);

		assertEvents(
				at(0)
						.beginCast(player, WRATH_OF_AIR_TOTEM)
						.beginGcd(player)
						.endCast(player, WRATH_OF_AIR_TOTEM)
						.decreasedResource(320, MANA, player, WRATH_OF_AIR_TOTEM)
						.effectApplied(WRATH_OF_AIR_TOTEM, player, 120),
				at(1.5)
						.endGcd(player),
				at(120)
						.effectExpired(WRATH_OF_AIR_TOTEM, player)
		);
	}

	@Test
	void sp_is_increased() {
		simulateBuffSpell(WRATH_OF_AIR_TOTEM);

		assertSpellPowerIncreasedBy(101);
	}
}
