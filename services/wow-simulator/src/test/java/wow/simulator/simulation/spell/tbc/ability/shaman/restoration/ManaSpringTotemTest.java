package wow.simulator.simulation.spell.tbc.ability.shaman.restoration;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcShamanSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.MANA_SPRING_TOTEM;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class ManaSpringTotemTest extends TbcShamanSpellSimulationTest {
	/*
	Summons a Mana Spring Totem with 5 health at the feet of the caster for 2 min that restores 20 mana every 2 seconds to group members within 20 yards.
	 */

	@Test
	void success() {
		player.setCurrentMana(120);
		player.cast(MANA_SPRING_TOTEM);

		player2.setCurrentMana(0);
		player3.setCurrentMana(0);
		player4.setCurrentMana(0);

		updateUntil(8);

		assertEvents(
				at(0)
						.beginCast(player, MANA_SPRING_TOTEM)
						.beginGcd(player)
						.endCast(player, MANA_SPRING_TOTEM)
						.decreasedResource(120, MANA, player, MANA_SPRING_TOTEM)
						.effectApplied(MANA_SPRING_TOTEM, player, 120),
				at(1.5)
						.endGcd(player),
				at(2)
						.increasedResource(20, MANA, player, MANA_SPRING_TOTEM)
						.increasedResource(20, MANA, player2, MANA_SPRING_TOTEM)
						.increasedResource(20, MANA, player3, MANA_SPRING_TOTEM)
						.increasedResource(20, MANA, player4, MANA_SPRING_TOTEM),
				at(4)
						.increasedResource(20, MANA, player, MANA_SPRING_TOTEM)
						.increasedResource(20, MANA, player2, MANA_SPRING_TOTEM)
						.increasedResource(20, MANA, player3, MANA_SPRING_TOTEM)
						.increasedResource(20, MANA, player4, MANA_SPRING_TOTEM),
				at(6)
						.increasedResource(20, MANA, player, MANA_SPRING_TOTEM)
						.increasedResource(20, MANA, player2, MANA_SPRING_TOTEM)
						.increasedResource(20, MANA, player3, MANA_SPRING_TOTEM)
						.increasedResource(20, MANA, player4, MANA_SPRING_TOTEM),
				at(8)
						.increasedResource(20, MANA, player, MANA_SPRING_TOTEM)
						.increasedResource(20, MANA, player2, MANA_SPRING_TOTEM)
						.increasedResource(20, MANA, player3, MANA_SPRING_TOTEM)
						.increasedResource(20, MANA, player4, MANA_SPRING_TOTEM)
		);
	}

	@Override
	protected void afterSetUp() {
		player.getParty().add(player2, player3, player4);
	}
}
