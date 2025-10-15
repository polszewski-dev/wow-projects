package wow.simulator.script.command;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.DoublePredicate;
import java.util.stream.Stream;

import static java.lang.Math.ceil;
import static java.lang.Math.max;
import static wow.test.commons.AbilityNames.*;

/**
 * User: POlszewski
 * Date: 2025-09-19
 */
class CastSpellExecutorTest extends CommandExecutorTest {
	@ParameterizedTest
	@MethodSource
	void allConditionsAreMet(TestData data) {
		var abilityName = data.abilityName();
		var timePredicate = data.timePredicate();
		var executor = getCastSpellExecutor(abilityName);
		var castTime = getCastTime(abilityName);
		var start = (int) ceil(max(castTime, 1.5)) + 1;
		var end = 180;

		for (int time = 0; time <= end; ++time) {
			snapshotAt(executor, time);
		}

		executor.execute();

		updateUntil(end);

		assertResultAt(0, true);

		for (int time = start; time <= end; ++time) {
			assertResultAt(time, timePredicate.test(time));
		}
	}

	static Stream<TestData> allConditionsAreMet() {
		return Stream.of(
			new TestData(CURSE_OF_DOOM, time -> time >= 60 && time <= 120),
			new TestData(CURSE_OF_AGONY, time -> time >= 24 && time <= 180 - 24),
			new TestData(CORRUPTION, time -> time >= 18 && time <= 180 - 2 - 18),
			new TestData(IMMOLATE, time -> time >= 15 && time <= 180 - 2 - 15),
			new TestData(SHADOW_BOLT, time -> time <= 180 - 3)
		);
	}

	record TestData(
			String abilityName,
			DoublePredicate timePredicate
	) {}
}