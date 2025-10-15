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
class CastSpellRankExecutorTest extends CommandExecutorTest {
	@ParameterizedTest
	@MethodSource
	void allConditionsAreMet(TestData data) {
		var abilityName = data.abilityName();
		var rank = data.rank();
		var timePredicate = data.timePredicate();
		var executor = getCastSpellRankExecutor(abilityName, rank);
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
			new TestData(CURSE_OF_DOOM, 1, time -> time >= 60 && time <= 120),
			new TestData(CURSE_OF_AGONY, 4, time -> time >= 24 && time <= 180 - 24),
			new TestData(CORRUPTION, 4, time -> time >= 18 && time <= 180 - 2 - 18),
			new TestData(IMMOLATE, 4, time -> time >= 15 && time <= 180 - 2 - 15),
			new TestData(SHADOW_BOLT, 4, time -> time <= 180 - 3)
		);
	}

	record TestData(
			String abilityName,
			int rank,
			DoublePredicate timePredicate
	) {}
}