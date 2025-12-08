package wow.simulator.simulation.spell.tbc.ability.mage.fire;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.simulation.spell.tbc.TbcMageSpellSimulationTest;
import wow.test.commons.TalentNames;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.DRAGONS_BREATH;

/**
 * User: POlszewski
 * Date: 2025-11-10
 */
class DragonsBreathTest extends TbcMageSpellSimulationTest {
	/*
	Targets in a cone in front of the caster take 680 to 790 Fire damage and are Disoriented for 3 sec. Any direct damaging attack will revive targets. Turns off your attack when used.
	 */

	@Test
	void success() {
		player.cast(DRAGONS_BREATH);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, DRAGONS_BREATH)
						.beginGcd(player)
						.endCast(player, DRAGONS_BREATH)
						.decreasedResource(700, MANA, player, DRAGONS_BREATH)
						.cooldownStarted(player, DRAGONS_BREATH, 20)
						.decreasedResource(735, HEALTH, target, DRAGONS_BREATH)
						.decreasedResource(735, HEALTH, target2, DRAGONS_BREATH)
						.decreasedResource(735, HEALTH, target3, DRAGONS_BREATH)
						.decreasedResource(735, HEALTH, target4, DRAGONS_BREATH),
				at(1.5)
						.endGcd(player),
				at(20)
						.cooldownExpired(player, DRAGONS_BREATH)
		);
	}

	@ParameterizedTest
	@MethodSource("spellDamageLevels")
	void damage_done(int spellDamage) {
		simulateDamagingSpell(DRAGONS_BREATH, spellDamage);

		assertDamageDone(DRAGONS_BREATH_INFO, target, spellDamage);
		assertDamageDone(DRAGONS_BREATH_INFO, target2, spellDamage);
		assertDamageDone(DRAGONS_BREATH_INFO, target3, spellDamage);
		assertDamageDone(DRAGONS_BREATH_INFO, target4, spellDamage);
	}

	@Override
	protected void afterSetUp() {
		enableTalent(TalentNames.DRAGONS_BREATH, 1);
	}
}
