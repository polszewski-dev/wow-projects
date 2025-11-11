package wow.simulator.simulation.spell.tbc.ability.priest.shadow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.simulator.simulation.spell.tbc.TbcPriestSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.MIND_FLAY;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class MindFlayTest extends TbcPriestSpellSimulationTest {
	/*
	Assault the target's mind with Shadow energy, causing 528 Shadow damage over 3 sec and slowing the target to 50% of their movement speed.
	 */

	@Test
	void success() {
		player.cast(MIND_FLAY);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, MIND_FLAY)
						.beginGcd(player)
						.endCast(player, MIND_FLAY)
						.decreasedResource(230, MANA, player, MIND_FLAY)
						.effectApplied(MIND_FLAY, target, 3)
						.beginChannel(player, MIND_FLAY, 3),
				at(1)
						.decreasedResource(176, HEALTH, target, MIND_FLAY),
				at(1.5)
						.endGcd(player),
				at(2)
						.decreasedResource(176, HEALTH, target, MIND_FLAY),
				at(3)
						.decreasedResource(176, HEALTH, target, MIND_FLAY)
						.effectExpired(MIND_FLAY, target)
						.endChannel(player, MIND_FLAY)
		);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 100, 1000 })
	void damage_done(int spellDamage) {
		simulateDamagingSpell(MIND_FLAY, spellDamage);

		assertDamageDone(MIND_FLAY_INFO, spellDamage);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.MIND_FLAY, 1);
	}
}
