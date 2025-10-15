package wow.simulator.script.command;

import org.junit.jupiter.api.Test;
import wow.character.model.script.ScriptCommandConditionParser;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.test.commons.AbilityNames.CURSE_OF_AGONY;
import static wow.test.commons.AbilityNames.CURSE_OF_DOOM;

/**
 * User: POlszewski
 * Date: 2025-10-08
 */
class ScriptConditionCheckerTest extends WarlockSpellSimulationTest {
	@Test
	void after_CoD_hits_condition_is_met() {
		snapshotAt(0.1, "Target.HasEffect(Curse of *)", CURSE_OF_DOOM);

		player.cast(CURSE_OF_DOOM);

		updateUntil(120);

		assertResultAt(0.1, true);
	}

	@Test
	void after_CoD_misses_CoA_can_be_casted() {
		missesOnlyOnFollowingRolls(0);

		snapshotAt(1.4, "Target.HasEffect(Curse of *)", CURSE_OF_AGONY);
		snapshotAt(1.6, "Target.HasEffect(Curse of *)", CURSE_OF_AGONY);

		player.cast(CURSE_OF_DOOM);
		player.cast(CURSE_OF_AGONY);

		updateUntil(120);

		assertResultAt(1.4, false);
		assertResultAt(1.6, true);
	}

	final TestSnapshots<Boolean> checkConditionSnapshots = new TestSnapshots<>();

	void snapshotAt(double time, String condition, String abilityName) {
		checkConditionSnapshots.makeSnapshotsAt(() -> checkCondition(condition, abilityName), time);
	}

	void assertResultAt(double time, boolean expectedValue) {
		var actualValue = checkConditionSnapshots.get(time);

		assertThat(actualValue)
				.withFailMessage("at time = %s allConditionsAreMet should return: %s".formatted(time, expectedValue))
				.isEqualTo(expectedValue);
	}

	boolean checkCondition(String condition, String abilityName) {
		var conditionParser = new ScriptCommandConditionParser(condition, Map.of());
		var parsedCondition = conditionParser.parse();
		var ability = player.getAbility(abilityName).orElseThrow();
		var conditionChecker = new ScriptConditionChecker(player, ability, player.getTarget());

		return conditionChecker.check(parsedCondition);
	}
}