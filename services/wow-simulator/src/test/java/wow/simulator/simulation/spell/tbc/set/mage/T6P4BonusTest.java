package wow.simulator.simulation.spell.tbc.set.mage;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2026-09-06
 */
class T6P4BonusTest extends TbcMageSpellSimulationTest {
	/*
	Increases the damage of your Fireball, Frostbolt, and Arcane Missiles abilities by 5%.
	 */

	@Test
	void fireball_damage_is_increased() {
		player.cast(FIREBALL);

		updateUntil(30);

		var totalSpellDamage = baseline.getStats().getSpellDamage();

		assertDamageDone(FIREBALL, FIREBALL_INFO.damage(totalSpellDamage), 5);
	}

	@Test
	void frostbolt_damage_is_increased() {
		player.cast(FROSTBOLT);

		updateUntil(30);

		var totalSpellDamage = baseline.getStats().getSpellDamage();

		assertDamageDone(FROSTBOLT, FROSTBOLT_INFO.damage(totalSpellDamage), 5);
	}

	@Test
	void arcane_missiles_damage_is_increased() {
		player.cast(ARCANE_MISSILES);

		updateUntil(30);

		var totalSpellDamage = baseline.getStats().getSpellDamage();

		assertDamageDone(ARCANE_MISSILES, ARCANE_MISSILES_INFO.damage(totalSpellDamage), 5);
	}

	@Override
	protected void afterSetUp() {
		equip("Mantle of the Tempest");
		equip("Boots of the Tempest");
		equip("Bracers of the Tempest");
		equip("Belt of the Tempest");
	}
}
