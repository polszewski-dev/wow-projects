package wow.simulator.simulation.spell.tbc.ability.mage.fire;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.FIREBALL;
import static wow.test.commons.AbilityNames.MOLTEN_ARMOR;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class MoltenArmorTest extends TbcMageSpellSimulationTest {
	/*
	Causes 75 Fire damage when hit, increases your chance to critically hit with spells by 3%, and reduces the chance you are critically hit by 5%. Only one type of Armor spell can be active on the Mage at any time. Lasts 30 min.
	 */

	@Test
	void success() {
		player.cast(MOLTEN_ARMOR);

		updateUntil(1800);

		assertEvents(
				at(0)
						.beginCast(player, MOLTEN_ARMOR)
						.beginGcd(player)
						.endCast(player, MOLTEN_ARMOR)
						.decreasedResource(630, MANA, player, MOLTEN_ARMOR)
						.effectApplied(MOLTEN_ARMOR, player, 1800),
				at(1.5)
						.endGcd(player),
				at(1800)
						.effectExpired(MOLTEN_ARMOR, player)
		);
	}

	@Test
	void intellect_is_increased() {
		player.cast(MOLTEN_ARMOR);
		player.cast(FIREBALL);

		updateUntil(30);

		var spellCritPctBefore = statsAt(0).getSpellCritPct();

		assertLastCritChance(spellCritPctBefore + 3);
	}
}
