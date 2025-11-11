package wow.simulator.simulation.spell.tbc.ability.shaman.restoration;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcShamanSpellSimulationTest;

import static wow.test.commons.AbilityNames.WATER_SHIELD;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class WaterShieldTest extends TbcShamanSpellSimulationTest {
	/*
	The caster is surrounded by 3 globes of water, granting 50 mana per 5 sec.
	When a spell, melee or ranged attack hits the caster, 204 mana is restored to the caster.
	This expends one water globe. Only one globe will activate every few seconds. Lasts 10 min.
	Only one Elemental Shield can be active on the Shaman at any one time.
	 */

	@Test
	void success() {
		player.cast(WATER_SHIELD);

		updateUntil(600);

		assertEvents(
				at(0)
						.beginCast(player, WATER_SHIELD)
						.beginGcd(player)
						.endCast(player, WATER_SHIELD)
						.cooldownStarted(player, WATER_SHIELD, 3.5)
						.effectApplied(WATER_SHIELD, player, 600),
				at(1.5)
						.endGcd(player),
				at(3.5)
						.cooldownExpired(player, WATER_SHIELD),
				at(600)
						.effectExpired(WATER_SHIELD, player)
		);
	}

	@Test
	void mp5_is_increased() {
		simulateBuffSpell(WATER_SHIELD);

		assertMp5IncreasedBy(50);
	}
}
