package wow.simulator.simulation.spell.tbc.ability.mage.arcane;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.MAGE_ARMOR;
import static wow.test.commons.AbilityNames.SCORCH;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class MageArmorTest extends TbcMageSpellSimulationTest {
	/*
	Increases your resistance to all magic by 18 and allows 30% of your mana regeneration to continue while casting. Only one type of Armor spell can be active on the Mage at any time. Lasts 30 min.
	 */

	@Test
	void success() {
		player.cast(MAGE_ARMOR);

		updateUntil(1800);

		assertEvents(
				at(0)
						.beginCast(player, MAGE_ARMOR)
						.beginGcd(player)
						.endCast(player, MAGE_ARMOR)
						.decreasedResource(575, MANA, player, MAGE_ARMOR)
						.effectApplied(MAGE_ARMOR, player, 1800),
				at(1.5)
						.endGcd(player),
				at(1800)
						.effectExpired(MAGE_ARMOR, player)
		);
	}

	@Test
	void pct_of_mana_regen_continues_while_casting() {
		handler.setIgnoreRegen(false);

		player.setCurrentMana(player.getMaxMana() / 2);

		player.cast(SCORCH);
		player.cast(SCORCH);
		player.cast(MAGE_ARMOR);
		player.cast(SCORCH);
		player.cast(SCORCH);

		updateUntil(16);

		assertEvents(
				testEvent -> testEvent.isManaGained() || testEvent.isBeginCast() || testEvent.isEndCast(),
				at(0)
						.beginCast(player, SCORCH, 1.5),
				at(1.5)
						.endCast(player, SCORCH)
						.beginCast(player, SCORCH, 1.5),
				at(3)
						.endCast(player, SCORCH)
						.beginCast(player, MAGE_ARMOR)
						.endCast(player, MAGE_ARMOR),
				at(4)
						.increasedResource(10, MANA, player, null),
				at(4.5)
						.beginCast(player, SCORCH, 1.5),
				at(6)
						.increasedResource(10, MANA, player, null)
						.endCast(player, SCORCH)
						.beginCast(player, SCORCH, 1.5),
				at(7.5)
						.endCast(player, SCORCH),
				at(8)
						.increasedResource(10, MANA, player, null),
				at(10)
						.increasedResource(10, MANA, player, null),
				at(12)
						.increasedResource(10, MANA, player, null),
				at(14)
						.increasedResource(34, MANA, player, null),
				at(16)
						.increasedResource(34, MANA, player, null)
		);
	}
}
