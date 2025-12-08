package wow.simulator.simulation.spell.tbc.ability.mage.fire;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.FIRE_BLAST;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class FireBlastTest extends TbcMageSpellSimulationTest {
	/*
	Blasts the enemy for 664 to 786 Fire damage.
	 */

	@Test
	void success() {
		player.cast(FIRE_BLAST);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, FIRE_BLAST)
						.beginGcd(player)
						.endCast(player, FIRE_BLAST)
						.decreasedResource(465, MANA, player, FIRE_BLAST)
						.cooldownStarted(player, FIRE_BLAST, 8)
						.decreasedResource(725, HEALTH, target, FIRE_BLAST),
				at(1.5)
						.endGcd(player),
				at(8)
						.cooldownExpired(player, FIRE_BLAST)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(FIRE_BLAST, spellDamage);

		assertDamageDone(FIRE_BLAST_INFO, spellDamage);
	}
}
