package wow.simulator.script;

import org.junit.jupiter.api.Test;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import static wow.simulator.util.TestEvent.BeginCast;
import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-09-20
 */
class ScriptExecutorTest extends WarlockSpellSimulationTest {
	@Test
	void execute() {
		var executor = new ScriptExecutor(player);

		executor.setupPlayer();

		updateUntil(60);

		assertEvents(
			testEvent -> testEvent instanceof BeginCast,
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

	@Override
	protected void afterSetUp() {
		super.afterSetUp();

		player.getBuild().setScript("/script/executor-test.txt");
	}
}