package wow.simulator.simulation.spell.ability.priest.shadow;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.MIND_BLAST;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class MindBlastTest extends PriestSpellSimulationTest {
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

	@Test
	void damageDone() {
		player.cast(MIND_BLAST);

		updateUntil(30);

		assertDamageDone(MIND_BLAST, MIND_BLAST_INFO.damage());
	}
}
