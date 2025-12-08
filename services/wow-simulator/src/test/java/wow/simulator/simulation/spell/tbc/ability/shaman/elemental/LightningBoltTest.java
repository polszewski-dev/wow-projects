package wow.simulator.simulation.spell.tbc.ability.shaman.elemental;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcShamanSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.LIGHTNING_BOLT;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class LightningBoltTest extends TbcShamanSpellSimulationTest {
	/*
	Casts a bolt of lightning at the target for 571 to 652 Nature damage.
	 */

	@Test
	void success() {
		player.cast(LIGHTNING_BOLT);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, LIGHTNING_BOLT, 2.5)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(2.5)
						.endCast(player, LIGHTNING_BOLT)
						.decreasedResource(300, MANA, player, LIGHTNING_BOLT)
						.decreasedResource(611, HEALTH, target, LIGHTNING_BOLT)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(LIGHTNING_BOLT, spellDamage);

		assertDamageDone(LIGHTNING_BOLT_INFO, spellDamage);
	}
}
