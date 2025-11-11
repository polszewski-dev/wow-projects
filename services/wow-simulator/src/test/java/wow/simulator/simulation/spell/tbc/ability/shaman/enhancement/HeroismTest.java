package wow.simulator.simulation.spell.tbc.ability.shaman.enhancement;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcShamanSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.HEROISM;
import static wow.test.commons.AbilityNames.LIGHTNING_BOLT;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class HeroismTest extends TbcShamanSpellSimulationTest {
	/*
	Increases melee, ranged, and spell casting speed by 30% for all party members. Lasts 40 sec.
	 */

	@Test
	void success() {
		player.cast(HEROISM);

		updateUntil(600);

		assertEvents(
				at(0)
						.beginCast(player, HEROISM)
						.beginGcd(player)
						.endCast(player, HEROISM)
						.decreasedResource(750, MANA, player, HEROISM)
						.cooldownStarted(player, HEROISM, 600)
						.effectApplied(HEROISM, player, 40)
						.effectApplied(HEROISM, player2, 40)
						.effectApplied(HEROISM, player3, 40)
						.effectApplied(HEROISM, player4, 40),
				at(1.5)
						.endGcd(player),
				at(40)
						.effectExpired(HEROISM, player)
						.effectExpired(HEROISM, player2)
						.effectExpired(HEROISM, player3)
						.effectExpired(HEROISM, player4),
				at(600)
						.cooldownExpired(player, HEROISM)
		);
	}

	@Test
	void cast_speed_is_increased() {
		player.cast(HEROISM);
		player.cast(LIGHTNING_BOLT);

		updateUntil(30);

		assertCastTime(LIGHTNING_BOLT, 2.5 / 1.3);
	}

	@Override
	protected void afterSetUp() {
		player.getParty().add(player2, player3, player4);
	}
}
