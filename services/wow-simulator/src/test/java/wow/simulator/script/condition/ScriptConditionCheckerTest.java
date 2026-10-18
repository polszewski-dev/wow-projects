package wow.simulator.script.condition;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.simulator.script.ScriptTest;
import wow.simulator.util.TestEvent;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static wow.test.commons.AbilityNames.FEL_ARMOR;

/**
 * User: POlszewski
 * Date: 2025-10-08
 */
class ScriptConditionCheckerTest extends ScriptTest {
	@ParameterizedTest
	@MethodSource("getScriptNames")
	void simple_conditions_are_true(String scriptName) {
		simulate(scriptName, 1);

		assertEvents(
				TestEvent::isBeginCast,
				at(0).beginCast(player, FEL_ARMOR)
		);
	}

	static Stream<String> getScriptNames() {
		return IntStream.rangeClosed(1, 15)
				.mapToObj("/script/condition/condition%s.txt"::formatted);
	}
}