package wow.simulator.simulation.spell.tbc.ability.priest.shadow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.SHADOW_WORD_PAIN;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class ShadowWordPainTest extends TbcPriestSpellSimulationTest {
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
						.beginGcd(player)
						.endCast(player, SHADOW_WORD_PAIN)
						.decreasedResource(575, MANA, player, SHADOW_WORD_PAIN)
						.effectApplied(SHADOW_WORD_PAIN, target, 18),
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

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(SHADOW_WORD_PAIN, spellDamage);

		assertDamageDone(SHADOW_WORD_PAIN_INFO, spellDamage);
	}
}
