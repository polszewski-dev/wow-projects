package wow.simulator.simulation.spell.ability.priest.shadow;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SHADOW_WORD_DEATH;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class ShadowWordDeathTest extends PriestSpellSimulationTest {
	/*
	A word of dark binding that inflicts 572 to 664 Shadow damage to the target.
	If the target is not killed by Shadow Word: Death, the caster takes damage equal to the damage inflicted upon the target.
	 */

	@Test
	void success() {
		player.cast(SHADOW_WORD_DEATH);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_WORD_DEATH)
						.beginGcd(player)
						.endCast(player, SHADOW_WORD_DEATH)
						.decreasedResource(309, MANA, player, SHADOW_WORD_DEATH)
						.cooldownStarted(player, SHADOW_WORD_DEATH, 12)
						.decreasedResource(618, HEALTH, target, SHADOW_WORD_DEATH),
				at(1.5)
						.endGcd(player),
				at(12)
						.cooldownExpired(player, SHADOW_WORD_DEATH)
		);
	}

	@Test
	void damageDone() {
		player.cast(SHADOW_WORD_DEATH);

		updateUntil(30);

		assertDamageDone(SHADOW_WORD_DEATH, SHADOW_WORD_DEATH_INFO.damage());
	}
}
