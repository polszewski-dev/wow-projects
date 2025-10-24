package wow.simulator.simulation.spell.tbc.activated;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.GroupCooldownId.POTION;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.test.commons.AbilityNames.SUPER_HEALING_POTION;

/**
 * User: POlszewski
 * Date: 2025-09-19
 */
class SuperHealingPotionTest extends TbcWarlockSpellSimulationTest {
	@Test
	void effectActivatesAndCooldownTriggers() {
		player.getConsumables().enable(SUPER_HEALING_POTION);

		setHealth(player, 1000);

		player.cast(SUPER_HEALING_POTION);

		updateUntil(150);

		assertEvents(
				at(0)
						.beginCast(player, SUPER_HEALING_POTION)
						.endCast(player, SUPER_HEALING_POTION)
						.cooldownStarted(player, SUPER_HEALING_POTION, 120)
						.cooldownStarted(player, POTION, 120)
						.increasedResource(2000, HEALTH, player, SUPER_HEALING_POTION),
				at(120)
						.cooldownExpired(player, SUPER_HEALING_POTION)
						.cooldownExpired(player, POTION)
		);
	}

	@Test
	void healthIsIncreased() {
		player.getConsumables().enable(SUPER_HEALING_POTION);

		setHealth(player, 1000);

		player.cast(SUPER_HEALING_POTION);

		updateUntil(0);

		assertThat(player.getCurrentHealth()).isEqualTo(1000 + 2000);
	}
}
