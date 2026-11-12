package wow.simulator.model.unit;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.tbc.TbcWarlockSpellSimulationTest;
import wow.simulator.util.TestEvent;

import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2026-11-14
 */
class PrependActionsScopeTest extends TbcWarlockSpellSimulationTest {
	@Test
	void with_the_scope_actions_are_placed_in_front_of_the_queue() {
		player.immediateAction(() -> {
			try (var scope = player.prependActionsScope()) {
				player.cast(CURSE_OF_AGONY);
				player.cast(CORRUPTION);
				player.cast(IMMOLATE);
			}
		});

		player.cast(SHADOW_BOLT);
		player.cast(SEARING_PAIN);

		updateUntil(30);

		assertEvents(
				TestEvent::isBeginCast,
				at(0).beginCast(player, CURSE_OF_AGONY),
				at(1.5).beginCast(player, CORRUPTION, 2),
				at(3.5).beginCast(player, IMMOLATE, 2),
				at(5.5).beginCast(player, SHADOW_BOLT, 3),
				at(8.5).beginCast(player, SEARING_PAIN, 1.5)
		);
	}

	@Test
	void without_the_scope_actions_are_placed_at_the_back_of_the_queue() {
		player.immediateAction(() -> {
			player.cast(SHADOW_BOLT);
			player.cast(SEARING_PAIN);
		});

		player.cast(CURSE_OF_AGONY);
		player.cast(CORRUPTION);
		player.cast(IMMOLATE);

		updateUntil(30);

		assertEvents(
				TestEvent::isBeginCast,
				at(0).beginCast(player, CURSE_OF_AGONY),
				at(1.5).beginCast(player, CORRUPTION, 2),
				at(3.5).beginCast(player, IMMOLATE, 2),
				at(5.5).beginCast(player, SHADOW_BOLT, 3),
				at(8.5).beginCast(player, SEARING_PAIN, 1.5)
		);
	}
}