package wow.simulator.simulation.spell.tbc.activated;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.GroupCooldownId.CONJURED_ITEM;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.test.commons.AbilityNames.MAJOR_HEALTHSTONE;

/**
 * User: POlszewski
 * Date: 2025-09-19
 */
class MajorHealthstoneTest extends TbcWarlockSpellSimulationTest {
	@Test
	void effectActivatesAndCooldownTriggers() {
		player.getConsumables().enable(MAJOR_HEALTHSTONE);

		setHealth(player, 1000);

		player.cast(MAJOR_HEALTHSTONE);

		updateUntil(150);

		assertEvents(
				at(0)
						.beginCast(player, MAJOR_HEALTHSTONE)
						.endCast(player, MAJOR_HEALTHSTONE)
						.cooldownStarted(player, MAJOR_HEALTHSTONE, 120)
						.cooldownStarted(player, CONJURED_ITEM, 120)
						.increasedResource(1440, HEALTH, player, MAJOR_HEALTHSTONE),
				at(120)
						.cooldownExpired(player, MAJOR_HEALTHSTONE)
						.cooldownExpired(player, CONJURED_ITEM)
		);
	}

	@Test
	void healthIsIncreased() {
		player.getConsumables().enable(MAJOR_HEALTHSTONE);

		setHealth(player, 1000);

		player.cast(MAJOR_HEALTHSTONE);

		updateUntil(0);

		assertThat(player.getCurrentHealth()).isEqualTo(1000 + 1440);
	}
}
