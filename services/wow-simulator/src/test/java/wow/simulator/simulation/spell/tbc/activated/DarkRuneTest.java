package wow.simulator.simulation.spell.tbc.activated;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.spell.GroupCooldownId.CONJURED_ITEM;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.DARK_RUNE;

/**
 * User: POlszewski
 * Date: 2025-09-19
 */
class DarkRuneTest extends TbcWarlockSpellSimulationTest {
	@Test
	void effectActivatesAndCooldownTriggers() {
		player.getConsumables().enable(DARK_RUNE);

		setHealth(player, 2000);
		setMana(player, 0);

		player.cast(DARK_RUNE);

		updateUntil(150);

		assertEvents(
				at(0)
						.beginCast(player, DARK_RUNE)
						.endCast(player, DARK_RUNE)
						.cooldownStarted(player, DARK_RUNE, 120)
						.cooldownStarted(player, CONJURED_ITEM, 120)
						.increasedResource(1200, MANA, player, DARK_RUNE)
						.decreasedResource(800, HEALTH, player, DARK_RUNE),
				at(120)
						.cooldownExpired(player, DARK_RUNE)
						.cooldownExpired(player, CONJURED_ITEM)
		);
	}

	@Test
	void manaIsIncreased() {
		player.getConsumables().enable(DARK_RUNE);

		setHealth(player, 2000);
		setMana(player, 0);

		player.cast(DARK_RUNE);

		updateUntil(0);

		assertThat(player.getCurrentHealth()).isEqualTo(1200);
		assertThat(player.getCurrentMana()).isEqualTo(1200);
	}
}
