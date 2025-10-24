package wow.simulator.simulation.spell.tbc.activated;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.GroupCooldownId.POTION;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.MAJOR_MANA_POTION;

/**
 * User: POlszewski
 * Date: 2025-09-19
 */
class MajorManaPotionTest extends TbcWarlockSpellSimulationTest {
	@Test
	void effectActivatesAndCooldownTriggers() {
		player.getConsumables().enable(MAJOR_MANA_POTION);

		setMana(player, 0);

		player.cast(MAJOR_MANA_POTION);

		updateUntil(150);

		assertEvents(
				at(0)
						.beginCast(player, MAJOR_MANA_POTION)
						.endCast(player, MAJOR_MANA_POTION)
						.cooldownStarted(player, MAJOR_MANA_POTION, 120)
						.cooldownStarted(player, POTION, 120)
						.increasedResource(1800, MANA, player, MAJOR_MANA_POTION),
				at(120)
						.cooldownExpired(player, MAJOR_MANA_POTION)
						.cooldownExpired(player, POTION)
		);
	}

	@Test
	void manaIsIncreased() {
		player.getConsumables().enable(MAJOR_MANA_POTION);

		setMana(player, 0);

		player.cast(MAJOR_MANA_POTION);

		updateUntil(0);

		assertThat(player.getCurrentMana()).isEqualTo(1800);
	}
}
