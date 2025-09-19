package wow.simulator.simulation.spell.activated;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.GroupCooldownId.CONJURED_ITEM;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.test.commons.AbilityNames.MASTER_HEALTHSTONE;

/**
 * User: POlszewski
 * Date: 2025-09-19
 */
class MasterHealthstoneTest extends WarlockSpellSimulationTest {
	@Test
	void effectActivatesAndCooldownTriggers() {
		player.getConsumables().enable(MASTER_HEALTHSTONE);

		setHealth(player, 1000);

		player.cast(MASTER_HEALTHSTONE);

		updateUntil(150);

		assertEvents(
				at(0)
						.beginCast(player, MASTER_HEALTHSTONE)
						.endCast(player, MASTER_HEALTHSTONE)
						.cooldownStarted(player, MASTER_HEALTHSTONE, 120)
						.cooldownStarted(player, CONJURED_ITEM, 120)
						.increasedResource(2496, HEALTH, player, MASTER_HEALTHSTONE),
				at(120)
						.cooldownExpired(player, MASTER_HEALTHSTONE)
						.cooldownExpired(player, CONJURED_ITEM)
		);
	}

	@Test
	void healthIsIncreased() {
		player.getConsumables().enable(MASTER_HEALTHSTONE);

		setHealth(player, 1000);

		player.cast(MASTER_HEALTHSTONE);

		updateUntil(0);

		assertThat(player.getCurrentHealth()).isEqualTo(1000 + 2496);
	}
}
