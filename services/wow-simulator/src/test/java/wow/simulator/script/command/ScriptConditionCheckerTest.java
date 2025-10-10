package wow.simulator.script.command;

import org.junit.jupiter.api.Test;
import wow.character.model.script.ScriptCommandConditionParser;
import wow.simulator.simulation.spell.WarlockSpellSimulationTest;

import java.util.Map;

import static wow.test.commons.AbilityNames.CURSE_OF_AGONY;
import static wow.test.commons.AbilityNames.CURSE_OF_DOOM;

/**
 * User: POlszewski
 * Date: 2025-10-08
 */
class ScriptConditionCheckerTest extends WarlockSpellSimulationTest {
	@Test
	void after_CoD_hits_condition_is_met() {
		player.cast(CURSE_OF_DOOM);

		assertResultAfter(0.1, () -> checkCondition("Target.HasEffect(Curse of *)", CURSE_OF_DOOM), true);

		updateUntil(120);
	}

	@Test
	void after_CoD_misses_CoA_can_be_casted() {
		missesOnlyOnFollowingRolls(0);

		player.cast(CURSE_OF_DOOM);
		player.cast(CURSE_OF_AGONY);

		assertResultAfter(1.4, () -> checkCondition("Target.HasEffect(Curse of *)", CURSE_OF_AGONY), false);
		assertResultAfter(1.6, () -> checkCondition("Target.HasEffect(Curse of *)", CURSE_OF_AGONY), true);

		updateUntil(120);
	}

	boolean checkCondition(String condition, String abilityName) {
		var conditionParser = new ScriptCommandConditionParser(condition, Map.of());
		var parsedCondition = conditionParser.parse();
		var ability = player.getAbility(abilityName).orElseThrow();
		var conditionChecker = new ScriptConditionChecker(player, ability, player.getTarget());

		return conditionChecker.check(parsedCondition);
	}
}