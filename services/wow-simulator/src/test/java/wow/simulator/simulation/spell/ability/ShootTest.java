package wow.simulator.simulation.spell.ability;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.test.commons.AbilityNames.SHOOT;

/**
 * User: POlszewski
 * Date: 2025-01-14
 */
class ShootTest extends PriestSpellSimulationTest {
	@Test
	void correctDamage() {
		player.cast(SHOOT);

		updateUntil(30);

		assertDamageDone(SHOOT, SHOOT_INFO.withDirect(136, 253, 0).damage());
	}

	@Test
	void correctCastTime() {
		player.cast(SHOOT);

		updateUntil(30);

		assertCastTime(SHOOT, 1.9);
	}

	@Override
	protected void afterSetUp() {
		equip("Starheart Baton");// 136	- 253 Arcane dmg, 1.9 speed
	}
}
