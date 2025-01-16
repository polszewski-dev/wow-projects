package wow.simulator.simulation.spell.activated;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.spell.AbilityId.TALISMAN_OF_ASCENDANCE;
import static wow.commons.model.spell.GroupCooldownId.TRINKET;

/**
 * User: POlszewski
 * Date: 2024-12-02
 */
class TalismanOfAscendanceTest extends WarlockSpellSimulationTest {
	/*
	Use: Your next 5 damage or healing spells cast within 20 seconds will grant a bonus of up to 40 damage and up to 75 healing,
	stacking up to 5 times. Expires after 6 damage or healing spells or 20 seconds, whichever occurs first. (1 Min Cooldown)
	 */
	@Test
	void effectIsApplied() {
		player.cast(TALISMAN_OF_ASCENDANCE);

		updateUntil(150);

		assertEvents(
				at(0)
						.beginCast(player, TALISMAN_OF_ASCENDANCE)
						.endCast(player, TALISMAN_OF_ASCENDANCE)
						.cooldownStarted(player, TALISMAN_OF_ASCENDANCE, 60)
						.cooldownStarted(player, TRINKET, 20)
						.effectApplied(TALISMAN_OF_ASCENDANCE, player, 20),
				at(20)
						.cooldownExpired(player, TRINKET)
						.effectExpired(TALISMAN_OF_ASCENDANCE, player),
				at(60)
						.cooldownExpired(player, TALISMAN_OF_ASCENDANCE)
		);
	}
	
	@Test
	void stacksIncrease() {
		player.cast(TALISMAN_OF_ASCENDANCE);

		for (int i = 0; i < 10; ++i) {
			player.cast(SHADOW_BOLT);
		}

		updateUntil(60);

		assertEvents(
				EventCollectingHandler.Event::isEffect,
				at(0)
						.effectApplied(TALISMAN_OF_ASCENDANCE, player, 20),
				at(3)
						.effectChargesDecreased(TALISMAN_OF_ASCENDANCE, player, 5)
						.effectStacksIncreased(TALISMAN_OF_ASCENDANCE, player, 1),
				at(6)
						.effectChargesDecreased(TALISMAN_OF_ASCENDANCE, player, 4)
						.effectStacksIncreased(TALISMAN_OF_ASCENDANCE, player, 2),
				at(9)
						.effectChargesDecreased(TALISMAN_OF_ASCENDANCE, player, 3)
						.effectStacksIncreased(TALISMAN_OF_ASCENDANCE, player, 3),
				at(12)
						.effectChargesDecreased(TALISMAN_OF_ASCENDANCE, player, 2)
						.effectStacksIncreased(TALISMAN_OF_ASCENDANCE, player, 4),
				at(15)
						.effectChargesDecreased(TALISMAN_OF_ASCENDANCE, player, 1)
						.effectStacksIncreased(TALISMAN_OF_ASCENDANCE, player, 5),
				at(18)
						.effectRemoved(TALISMAN_OF_ASCENDANCE, player)
		);
	}

	@Override
	protected void afterSetUp() {
		equip(TALISMAN_OF_ASCENDANCE.getName(), TRINKET_1);
	}
}
