package wow.simulator.simulation;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;
import wow.simulator.util.TestEvent;

import static wow.commons.model.spell.ResourceType.MANA;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2025-10-13
 */
class RegenActionTest extends WarlockSpellSimulationTest {
	@Test
	void uninterrupted_mana_no_mp5() {
		setMana(player, 500);

		updateUntil(10);

		assertEvents(
				at(2).increasedResource(30, MANA, player, null),
				at(4).increasedResource(30, MANA, player, null),
				at(6).increasedResource(30, MANA, player, null),
				at(8).increasedResource(30, MANA, player, null),
				at(10).increasedResource(30, MANA, player, null)
		);
	}

	@Test
	void uninterrupted_mana_50_mp5() {
		setMana(player, 500);

		player.addHiddenEffect("Bonus Mp5", 50);

		updateUntil(10);

		assertEvents(
				at(2).increasedResource(50, MANA, player, null),
				at(4).increasedResource(50, MANA, player, null),
				at(6).increasedResource(50, MANA, player, null),
				at(8).increasedResource(50, MANA, player, null),
				at(10).increasedResource(50, MANA, player, null)
		);
	}

	@Test
	void interrupted_mana_no_mp5() {
		setMana(player, 500);

		player.cast(SHADOW_BOLT);

		updateUntil(10);

		assertEvents(
				TestEvent::isManaGained,
				at(2).increasedResource(30, MANA, player, null),
				at(8).increasedResource(30, MANA, player, null),
				at(10).increasedResource(30, MANA, player, null)
		);
	}

	@Test
	void interrupted_mana_50_mp5() {
		setMana(player, 500);

		player.addHiddenEffect("Bonus Mp5", 50);

		player.cast(SHADOW_BOLT);

		updateUntil(10);

		assertEvents(
				TestEvent::isManaGained,
				at(2).increasedResource(50, MANA, player, null),
				at(4).increasedResource(20, MANA, player, null),
				at(6).increasedResource(20, MANA, player, null),
				at(8).increasedResource(50, MANA, player, null),
				at(10).increasedResource(50, MANA, player, null)
		);
	}

	@Override
	protected void afterSetUp() {
		handler.setIgnoreRegen(false);
	}
}
