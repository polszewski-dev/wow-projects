package wow.simulator.simulation.spell.ability.priest.shadow;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.AbilityId.SHADOW_WORD_PAIN;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class ShadowWordPainTest extends PriestSpellSimulationTest {
	/*
	A word of darkness that causes 1236 Shadow damage over 18 sec.
	 */

	@Test
	void success() {
		player.cast(SHADOW_WORD_PAIN);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_WORD_PAIN)
						.endCast(player, SHADOW_WORD_PAIN)
						.decreasedResource(575, MANA, player, SHADOW_WORD_PAIN)
						.effectApplied(SHADOW_WORD_PAIN, target, 18)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN),
				at(6)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN),
				at(9)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN),
				at(12)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN),
				at(15)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN),
				at(18)
						.decreasedResource(206, HEALTH, target, SHADOW_WORD_PAIN)
						.effectExpired(SHADOW_WORD_PAIN, target)
		);
	}

	@Test
	void damageDone() {
		player.cast(SHADOW_WORD_PAIN);

		updateUntil(30);

		assertDamageDone(SHADOW_WORD_PAIN, 1236);
	}
}
