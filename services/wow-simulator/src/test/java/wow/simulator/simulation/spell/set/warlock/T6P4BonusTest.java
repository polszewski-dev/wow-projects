package wow.simulator.simulation.spell.set.warlock;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.INCINERATE;
import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2025-01-24
 */
class T6P4BonusTest extends WarlockSpellSimulationTest {
	/*
    Increases the damage dealt by your Shadow Bolt and Incinerate abilities by 6%.
	*/

	@Test
	void shadowBoltDamageIsIncreased() {
		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertDamageDone(SHADOW_BOLT, SHADOW_BOLT_INFO.damage(totalSpellDamage), 6);
	}

	@Test
	void incinerateDamageIsIncreased() {
		player.cast(INCINERATE);

		updateUntil(30);

		assertDamageDone(INCINERATE, INCINERATE_INFO.damage(totalSpellDamage), 6);
	}

	@Override
	protected void afterSetUp() {
		equip("Gloves of the Malefic");
		equip("Hood of the Malefic");
		equip("Leggings of the Malefic");
		equip("Mantle of the Malefic");
	}
}