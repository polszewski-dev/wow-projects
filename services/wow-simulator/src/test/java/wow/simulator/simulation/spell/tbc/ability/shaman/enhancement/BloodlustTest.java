package wow.simulator.simulation.spell.tbc.ability.shaman.enhancement;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcShamanSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.BLOODLUST;
import static wow.test.commons.AbilityNames.LIGHTNING_BOLT;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class BloodlustTest extends TbcShamanSpellSimulationTest {
	/*
	Increases melee, ranged, and spell casting speed by 30% for all party members. Lasts 40 sec.
	 */

	@Test
	void success() {
		player.cast(BLOODLUST);

		updateUntil(600);

		assertEvents(
				at(0)
						.beginCast(player, BLOODLUST)
						.beginGcd(player)
						.endCast(player, BLOODLUST)
						.decreasedResource(750, MANA, player, BLOODLUST)
						.cooldownStarted(player, BLOODLUST, 600)
						.effectApplied(BLOODLUST, player, 40)
						.effectApplied(BLOODLUST, player2, 40)
						.effectApplied(BLOODLUST, player3, 40)
						.effectApplied(BLOODLUST, player4, 40),
				at(1.5)
						.endGcd(player),
				at(40)
						.effectExpired(BLOODLUST, player)
						.effectExpired(BLOODLUST, player2)
						.effectExpired(BLOODLUST, player3)
						.effectExpired(BLOODLUST, player4),
				at(600)
						.cooldownExpired(player, BLOODLUST)
		);
	}

	@Test
	void cast_speed_is_increased() {
		player.cast(BLOODLUST);
		player.cast(LIGHTNING_BOLT);

		updateUntil(30);

		assertCastTime(LIGHTNING_BOLT, 2.5 / 1.3);
	}

	@Override
	protected void afterSetUp() {
		player.getParty().add(player2, player3, player4);
	}
}
