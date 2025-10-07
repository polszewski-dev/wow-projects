package wow.character.model.script;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.character.model.script.ScriptCommandCondition.*;

/**
 * User: POlszewski
 * Date: 2025-10-08
 */
class ScriptCommandConditionParserTest {
	@ParameterizedTest
	@MethodSource("getTestData")
	void testValues(TestData data) {
		var parser = new ScriptCommandConditionParser(data.string);

		var parsed = parser.parse();

		assertThat(parsed).isEqualTo(data.condition);
	}

	static List<TestData> getTestData() {
		return TEST_DATA;
	}

	static final List<TestData> TEST_DATA = List.of(
			testData(
					"Caster.HasEffect(Fel Armor)",
					new CasterHasEffect("Fel Armor")
			),
			testData(
					"Caster.HasEffect(* Armor)",
					new CasterHasEffect("* Armor")
			),
			testData(
					"Target.HasEffect(Curse of Doom)",
					new TargetHasEffect("Curse of Doom")
			),
			testData(
					"Target.HasEffect(Curse of *)",
					new TargetHasEffect("Curse of *")
			),
			testData(
					"~Target.HasEffect(Curse of *)",
					new Not(new TargetHasEffect("Curse of *"))
			)
	);

	record TestData(String string, ScriptCommandCondition condition) {}

	static TestData testData(String string, ScriptCommandCondition condition) {
		return new TestData(string, condition);
	}
}