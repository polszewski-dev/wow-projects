package wow.simulator.simulation.spell.tbc.activated;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.GroupCooldownId.POTION;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.SUPER_MANA_POTION;

/**
 * User: POlszewski
 * Date: 2025-09-19
 */
class SuperManaPotionTest extends TbcWarlockSpellSimulationTest {
	@Test
	void effectActivatesAndCooldownTriggers() {
		player.getConsumables().enable(SUPER_MANA_POTION);

		setMana(player, 0);

		player.cast(SUPER_MANA_POTION);

		updateUntil(150);

		assertEvents(
				at(0)
						.beginCast(player, SUPER_MANA_POTION)
						.endCast(player, SUPER_MANA_POTION)
						.cooldownStarted(player, SUPER_MANA_POTION, 120)
						.cooldownStarted(player, POTION, 120)
						.increasedResource(2400, MANA, player, SUPER_MANA_POTION),
				at(120)
						.cooldownExpired(player, SUPER_MANA_POTION)
						.cooldownExpired(player, POTION)
		);
	}

	@Test
	void manaIsIncreased() {
		player.getConsumables().enable(SUPER_MANA_POTION);

		setMana(player, 0);

		player.cast(SUPER_MANA_POTION);

		updateUntil(0);

		assertThat(player.getCurrentMana()).isEqualTo(2400);
	}
}
