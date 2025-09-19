package wow.simulator.script.command;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.DoubleFunction;
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
	void allConditionsAreMet(AllConditionsAreMetTestData data) {
		var abilityName = data.abilityName();
		var timePredicate = data.timePredicate();
		var executor = getCastSpellExecutor(abilityName);
		var castTime = getCastTime(abilityName);
		var start = (int) ceil(max(castTime, 1.5)) + 1;//todo
		var end = 180;

		assertResultAfter(0, executor::allConditionsAreMet, true);

		executor.execute();

		for (int delay = start; delay <= end; ++delay) {
			assertMappedResultAfter(delay, executor::allConditionsAreMet, timePredicate);
		}

		updateUntil(end);
	}

	static Stream<AllConditionsAreMetTestData> allConditionsAreMet() {
		return Stream.of(
			new AllConditionsAreMetTestData(CURSE_OF_DOOM, time -> time >= 60 && time <= 120),
			new AllConditionsAreMetTestData(CURSE_OF_AGONY, time -> time >= 24 && time <= 180 - 24),
			new AllConditionsAreMetTestData(CORRUPTION, time -> time >= 18 && time <= 180 - 2 - 18),
			new AllConditionsAreMetTestData(IMMOLATE, time -> time >= 15 && time <= 180 - 2 - 15),
			new AllConditionsAreMetTestData(SHADOW_BOLT, time -> time <= 180 - 3)
		);
	}

	record AllConditionsAreMetTestData(
			String abilityName,
			DoubleFunction<Boolean> timePredicate
	) {}
}