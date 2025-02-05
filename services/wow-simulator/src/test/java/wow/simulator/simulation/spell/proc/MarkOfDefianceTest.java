package wow.simulator.simulation.spell.proc;

import org.junit.jupiter.api.Test;
import wow.commons.model.spell.CooldownId;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;
import wow.simulator.util.TestEvent;

import static wow.commons.model.categorization.ItemSlot.TRINKET_1;
import static wow.commons.model.spell.AbilityId.SHADOW_BOLT;
import static wow.commons.model.spell.ResourceType.HEALTH;
import static wow.commons.model.spell.ResourceType.MANA;

/**
 * User: POlszewski
 * Date: 2024-11-16
 */
class MarkOfDefianceTest extends WarlockSpellSimulationTest {
	@Test
	void eventAndItsCooldownAreTriggered() {
		eventsOnlyOnFollowingRolls(0);

		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertEvents(
				at(0)
						.beginCast(player, SHADOW_BOLT, 3)
						.beginGcd(player),
				at(1.5)
						.endGcd(player),
				at(3)
						.endCast(player, SHADOW_BOLT)
						.decreasedResource(420, MANA, player, SHADOW_BOLT)
						.cooldownStarted(player, cooldownId, 17)
						.increasedResource(150, MANA, player, "Mark of Defiance - proc #1 - triggered")
						.decreasedResource(602, HEALTH, false, target, SHADOW_BOLT),
				at(20)
						.cooldownExpired(player, cooldownId)
		);
	}

	@Test
	void cooldownIsTriggeredAfterItExpires() {
		eventsOnlyOnFollowingRolls(0, 1, 2, 3, 4, 5, 6);

		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);
		player.cast(SHADOW_BOLT);

		updateUntil(30);

		assertEvents(
				TestEvent::isCooldown,
				at(3)
						.cooldownStarted(player, cooldownId, 17),
				at(20)
						.cooldownExpired(player, cooldownId),
				at(21)
						.cooldownStarted(player, cooldownId, 17)
		);
	}

	CooldownId cooldownId = CooldownId.of(100127922);

	@Override
	protected void afterSetUp() {
		equip(27924, TRINKET_1);
	}
}
