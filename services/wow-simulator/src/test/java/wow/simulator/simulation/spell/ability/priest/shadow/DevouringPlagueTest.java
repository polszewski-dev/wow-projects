package wow.simulator.simulation.spell.ability.priest.shadow;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.PriestSpellSimulationTest;

import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.DEVOURING_PLAGUE;

/**
 * User: POlszewski
 * Date: 2025-01-19
 */
class DevouringPlagueTest extends PriestSpellSimulationTest {
	/*
	Afflicts the target with a disease that causes 1216 Shadow damage over 24 sec.  Damage caused by the Devouring Plague heals the caster.
	 */

	@Test
	void success() {
		player.cast(DEVOURING_PLAGUE);

		updateUntil(180);

		assertEvents(
				at(0)
						.beginCast(player, DEVOURING_PLAGUE)
						.beginGcd(player)
						.endCast(player, DEVOURING_PLAGUE)
						.decreasedResource(1145, MANA, player, DEVOURING_PLAGUE)
						.cooldownStarted(player, DEVOURING_PLAGUE, 180)
						.effectApplied(DEVOURING_PLAGUE, target, 24),
				at(1.5)
						.endGcd(player),
				at(3)
						.decreasedResource(152, HEALTH, target, DEVOURING_PLAGUE)
						.increasedResource(152, HEALTH, player, DEVOURING_PLAGUE),
				at(6)
						.decreasedResource(152, HEALTH, target, DEVOURING_PLAGUE)
						.increasedResource(152, HEALTH, player, DEVOURING_PLAGUE),
				at(9)
						.decreasedResource(152, HEALTH, target, DEVOURING_PLAGUE)
						.increasedResource(152, HEALTH, player, DEVOURING_PLAGUE),
				at(12)
						.decreasedResource(152, HEALTH, target, DEVOURING_PLAGUE)
						.increasedResource(152, HEALTH, player, DEVOURING_PLAGUE),
				at(15)
						.decreasedResource(152, HEALTH, target, DEVOURING_PLAGUE)
						.increasedResource(152, HEALTH, player, DEVOURING_PLAGUE),
				at(18)
						.decreasedResource(152, HEALTH, target, DEVOURING_PLAGUE)
						.increasedResource(152, HEALTH, player, DEVOURING_PLAGUE),
				at(21)
						.decreasedResource(152, HEALTH, target, DEVOURING_PLAGUE)
						.increasedResource(152, HEALTH, player, DEVOURING_PLAGUE),
				at(24)
						.decreasedResource(152, HEALTH, target, DEVOURING_PLAGUE)
						.increasedResource(152, HEALTH, player, DEVOURING_PLAGUE)
						.effectExpired(DEVOURING_PLAGUE, target),
				at(180)
						.cooldownExpired(player, DEVOURING_PLAGUE)
		);
	}

	@Test
	void damageDone() {
		player.cast(DEVOURING_PLAGUE);

		updateUntil(30);

		assertDamageDone(DEVOURING_PLAGUE, DEVOURING_PLAGUE_INFO.damage());
	}
}
