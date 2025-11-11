package wow.simulator.simulation.spell.tbc.ability.mage.frost;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.FROSTBOLT;
import static wow.test.commons.AbilityNames.ICY_VEINS;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class IcyVeinsTest extends TbcMageSpellSimulationTest {
	/*
	Hastens your spellcasting, increasing spell casting speed by 20% and gives you 100% chance to avoid interruption caused by damage while casting. Lasts 20 sec.
	 */

	@Test
	void success() {
		player.cast(ICY_VEINS);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, ICY_VEINS)
						.endCast(player, ICY_VEINS)
						.decreasedResource(125, MANA, player, ICY_VEINS)
						.cooldownStarted(player, ICY_VEINS, 180)
						.effectApplied(ICY_VEINS, player, 20),
				at(20)
						.effectExpired(ICY_VEINS, player),
				at(180)
						.cooldownExpired(player, ICY_VEINS)
		);
	}

	@Test
	void haste_is_increased() {
		simulateBuffSpell(ICY_VEINS);

		assertSpellHastePctIncreasedBy(20);
	}

	@Test
	void cast_time_is_reduced() {
		player.cast(ICY_VEINS);
		player.cast(FROSTBOLT);

		updateUntil(30);

		assertCastTime(FROSTBOLT, 2.5);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.ICY_VEINS, 1);
	}
}
