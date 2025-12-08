package wow.simulator.simulation.spell.tbc.ability.shaman.elemental;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcShamanSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.CHAIN_LIGHTNING;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class ChainLightningTest extends TbcShamanSpellSimulationTest {
	/*
	Hurls a lightning bolt at the enemy, dealing 734 to 838 Nature damage and then jumping to additional nearby enemies.
	Each jump reduces the damage by 30%. Affects 3 total targets.
	 */

	@Test
	void success() {
		player.cast(CHAIN_LIGHTNING);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, CHAIN_LIGHTNING, 2)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2)
						.endCast(player, CHAIN_LIGHTNING)
						.decreasedResource(760, MANA, player, CHAIN_LIGHTNING)
						.cooldownStarted(player, CHAIN_LIGHTNING, 6)
						.decreasedResource(786, HEALTH, target, CHAIN_LIGHTNING),
				at(8)
						.cooldownExpired(player, CHAIN_LIGHTNING)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(CHAIN_LIGHTNING, spellDamage);

		assertDamageDone(CHAIN_LIGHTNING_INFO, spellDamage);
	}
}
