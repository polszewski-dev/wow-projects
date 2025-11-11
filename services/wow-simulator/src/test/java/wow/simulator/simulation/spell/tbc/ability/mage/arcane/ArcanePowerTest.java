package wow.simulator.simulation.spell.tbc.ability.mage.arcane;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.test.commons.AbilityNames.ARCANE_POWER;
import static wow.test.commons.AbilityNames.FIREBALL;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class ArcanePowerTest extends TbcMageSpellSimulationTest {
	/*
	When activated, your spells deal 30% more damage while costing 30% more mana to cast. This effect lasts 15 sec.
	 */

	@Test
	void success() {
		player.cast(ARCANE_POWER);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, ARCANE_POWER)
						.endCast(player, ARCANE_POWER)
						.cooldownStarted(player, ARCANE_POWER, 180)
						.effectApplied(ARCANE_POWER, player, 15),
				at(15)
						.effectExpired(ARCANE_POWER, player),
				at(180)
						.cooldownExpired(player, ARCANE_POWER)
		);
	}

	@Test
	void damage_is_increased() {
		player.cast(ARCANE_POWER);
		player.cast(FIREBALL);

		updateUntil(30);

		assertDamageDone(FIREBALL, FIREBALL_INFO.damage(), 30);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.ARCANE_POWER, 1);
	}
}
