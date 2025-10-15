package wow.simulator.simulation.spell.ability.warlock.demonology;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.FEL_ARMOR;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
class FelArmorTest extends WarlockSpellSimulationTest {
	/*
	Surrounds the caster with fel energy, increasing the amount of health generated through spells and effects by 20%
	and increasing spell damage by up to 100.  Only one type of Armor spell can be active on the Warlock at any time. Lasts 30 min.
	 */

	@Test
	void success() {
		player.cast(FEL_ARMOR);

		updateUntil(30 * 60);

		assertEvents(
				at(0)
						.beginCast(player, FEL_ARMOR)
						.beginGcd(player)
						.endCast(player, FEL_ARMOR)
						.decreasedResource(725, MANA, player, FEL_ARMOR)
						.effectApplied(FEL_ARMOR, player, 30 * 60),
				at(1.5)
						.endGcd(player),
				at(30 * 60)
						.effectExpired(FEL_ARMOR, player)
		);
	}

	@Test
	void modifierIsTakenIntoAccount() {
		player.cast(FEL_ARMOR);

		updateUntil(30);

		var spellDamageBefore = statsAt(0).getSpellDamage();
		var spellDamageAfter = statsAt(1).getSpellDamage();

		assertThat(spellDamageAfter).isEqualTo(spellDamageBefore + 100);
	}
}
