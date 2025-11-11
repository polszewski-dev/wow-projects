package wow.simulator.simulation.spell.tbc.ability.shaman.elemental;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcShamanSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.FLAME_SHOCK;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class FlameShockTest extends TbcShamanSpellSimulationTest {
	/*
	Instantly sears the target with fire, causing 377 Fire damage immediately and 420 Fire damage over 12 sec.
	 */

	@Test
	void success() {
		player.cast(FLAME_SHOCK);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, FLAME_SHOCK)
						.beginGcd(player)
						.endCast(player, FLAME_SHOCK)
						.decreasedResource(500, MANA, player, FLAME_SHOCK)
						.cooldownStarted(player, FLAME_SHOCK, 6)
						.decreasedResource(377, HEALTH, target, FLAME_SHOCK)
						.effectApplied(FLAME_SHOCK, target, 12),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(105, HEALTH, target, FLAME_SHOCK),
				at(6)
						.cooldownExpired(player, FLAME_SHOCK)
						.decreasedResource(105, HEALTH, target, FLAME_SHOCK),
				at(9)
						.decreasedResource(105, HEALTH, target, FLAME_SHOCK),
				at(12)
						.decreasedResource(105, HEALTH, target, FLAME_SHOCK)
						.effectExpired(FLAME_SHOCK, target)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(FLAME_SHOCK, spellDamage);

		assertDamageDone(FLAME_SHOCK_INFO, spellDamage);
	}
}
