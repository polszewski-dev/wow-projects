package wow.simulator.simulation.spell.tbc.ability.priest.shadow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.SHADOW_WORD_DEATH;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class ShadowWordDeathTest extends TbcPriestSpellSimulationTest {
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

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(SHADOW_WORD_DEATH, spellDamage);

		assertDamageDone(SHADOW_WORD_DEATH_INFO, spellDamage);
	}
}
