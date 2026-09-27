package wow.simulator.simulation.spell.tbc.ability.mage.frost;

import org.junit.jupiter.api.Test;
import wow.commons.model.Duration;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.COLD_SNAP;
import static wow.test.commons.AbilityNames.ICY_VEINS;

/**
 * User: POlszewski
 * Date: 2026-09-26
 */
class ColdSnapTest extends TbcMageSpellSimulationTest {
	/*
	When activated, this spell finishes the cooldown on all Frost spells you recently cast.
	 */

	@Test
	void success() {
		player.cast(COLD_SNAP);

		updateUntil(600);

		assertEvents(
				at(0)
						.beginCast(player, COLD_SNAP)
						.endCast(player, COLD_SNAP)
						.cooldownStarted(player, COLD_SNAP, 480),
				at(480)
						.cooldownExpired(player, COLD_SNAP)
		);
	}

	@Test
	void cooldowns_are_reset() {
		enableTalent(TalentNames.ICY_VEINS);

		player.cast(ICY_VEINS);
		player.idleFor(Duration.seconds(30));
		player.cast(COLD_SNAP);
		player.cast(ICY_VEINS);

		updateUntil(600);

		assertEvents(
				at(0)
						.beginCast(player, ICY_VEINS)
						.endCast(player, ICY_VEINS)
						.decreasedResource(125, MANA, player, ICY_VEINS)
						.cooldownStarted(player, ICY_VEINS, 180)
						.effectApplied(ICY_VEINS, player, 20),
				at(20)
						.effectExpired(ICY_VEINS, player),
				at(30)
						.beginCast(player, COLD_SNAP)
						.endCast(player, COLD_SNAP)
						.cooldownStarted(player, COLD_SNAP, 480)
						.cooldownExpired(player, ICY_VEINS)
						.beginCast(player, ICY_VEINS)
						.endCast(player, ICY_VEINS)
						.decreasedResource(125, MANA, player, ICY_VEINS)
						.cooldownStarted(player, ICY_VEINS, 180)
						.effectApplied(ICY_VEINS, player, 20),
				at(50)
						.effectExpired(ICY_VEINS, player),
				at(210)
						.cooldownExpired(player, ICY_VEINS),
				at(510)
						.cooldownExpired(player, COLD_SNAP)
		);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.COLD_SNAP);
	}
}
