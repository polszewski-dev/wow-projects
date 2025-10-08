package wow.simulator.script;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;
import wow.simulator.util.TestEvent;

import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-09-20
 */
class ScriptExecutorTest extends WarlockSpellSimulationTest {
	@Test
	void cast_dots_and_then_SB() {
		simulate("/script/executor-test.txt", 60);

		assertEvents(
				TestEvent::isBeginCast,
				at(0).beginCast(player, CURSE_OF_AGONY),
				at(1.5).beginCast(player, CORRUPTION, 2),
				at(3.5).beginCast(player, IMMOLATE, 2),
				at(5.5).beginCast(player, SHADOW_BOLT, 3),
				at(8.5).beginCast(player, SHADOW_BOLT, 3),
				at(11.5).beginCast(player, SHADOW_BOLT, 3),
				at(14.5).beginCast(player, SHADOW_BOLT, 3),
				at(17.5).beginCast(player, SHADOW_BOLT, 3),
				at(20.5).beginCast(player, CORRUPTION, 2),
				at(22.5).beginCast(player, IMMOLATE, 2),
				at(24.5).beginCast(player, CURSE_OF_AGONY),
				at(26).beginCast(player, SHADOW_BOLT, 3),
				at(29).beginCast(player, SHADOW_BOLT, 3),
				at(32).beginCast(player, SHADOW_BOLT, 3),
				at(35).beginCast(player, SHADOW_BOLT, 3),
				at(38).beginCast(player, IMMOLATE, 2),
				at(40).beginCast(player, CORRUPTION, 2),
				at(42).beginCast(player, SHADOW_BOLT, 3),
				at(45).beginCast(player, SHADOW_BOLT, 3),
				at(48).beginCast(player, SHADOW_BOLT, 3),
				at(51).beginCast(player, SHADOW_BOLT, 3),
				at(54).beginCast(player, SHADOW_BOLT, 3),
				at(57).beginCast(player, SHADOW_BOLT, 3)
		);
	}

	@Test
	void recast_CoA_after_CoD_missed() {
		missesOnlyOnFollowingRolls(0);
		simulate("/script/executor-test2.txt", 150);

		assertEvents(
				this::onlyCurses,
				at(0)
						.beginCast(player, CURSE_OF_DOOM)
						.spellResisted(player, CURSE_OF_DOOM, target),
				at(1.5)
						.beginCast(player, CURSE_OF_AGONY),
				at(27)
						.beginCast(player, CURSE_OF_AGONY),
				at(52.5)
						.beginCast(player, CURSE_OF_AGONY),
				at(78)
						.beginCast(player, CURSE_OF_DOOM)
		);
	}

	@Test
	void recast_CoA_after_CoD_missed_and_CoA_casted_only_twice() {
		missesOnlyOnFollowingRolls(0);
		simulate("/script/executor-test3.txt", 125);

		assertEvents(
				this::onlyCurses,
				at(0)
						.beginCast(player, CURSE_OF_DOOM)
						.spellResisted(player, CURSE_OF_DOOM, target),
				at(1.5)
						.beginCast(player, CURSE_OF_AGONY),
				at(27)
						.beginCast(player, CURSE_OF_AGONY),
				at(61.5)
						.beginCast(player, CURSE_OF_DOOM)
		);
	}

	@Test
	void CoA_cast_before_simulation_ends() {
		simulate("/script/executor-test3.txt", 90);

		assertEvents(
				this::onlyCurses,
				at(0)
						.beginCast(player, CURSE_OF_DOOM),
				at(61.5)
						.beginCast(player, CURSE_OF_AGONY)
		);
	}

	void simulate(String script, int time) {
		player.getBuild().setScript(script);

		var executor = new ScriptExecutor(player);

		executor.setupPlayer();

		updateUntil(time);
	}

	boolean onlyCurses(TestEvent testEvent) {
		return testEvent.isBeginCast(CURSE_OF_DOOM) ||
				testEvent.isBeginCast(CURSE_OF_AGONY) ||
				testEvent.isSpellResisted();
	}
}