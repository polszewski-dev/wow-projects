package wow.simulator.simulation.spell.tbc.ability.priest.shadow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.MIND_BLAST;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class MindBlastTest extends TbcPriestSpellSimulationTest {
	/*
	Blasts the target for 711 to 752 Shadow damage.
	 */

	@Test
	void success() {
		player.cast(MIND_BLAST);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, MIND_BLAST, 1.5)
						.beginGcd(player),
				at(1.5)
						.endCast(player, MIND_BLAST)
						.decreasedResource(450, MANA, player, MIND_BLAST)
						.cooldownStarted(player, MIND_BLAST, 8)
						.decreasedResource(731, HEALTH, target, MIND_BLAST)
						.endGcd(player),
				at(9.5)
						.cooldownExpired(player, MIND_BLAST)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(MIND_BLAST, spellDamage);

		assertDamageDone(MIND_BLAST_INFO, spellDamage);
	}
}
