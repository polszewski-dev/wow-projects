package wow.character.model.script;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.character.constant.AbilityIds.CURSE_OF_DOOM;
import static wow.character.model.script.ScriptCommandCondition.*;

/**
 * User: POlszewski
 * Date: 2025-10-08
 */
class ScriptCommandConditionParserTest {
	@ParameterizedTest
	@MethodSource("getTestData")
	void testValues(TestData data) {
		var parser = new ScriptCommandConditionParser(data.string, Map.of());

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
			),
			testData(
					"CanCastMoreBeforeSimulationEnds(Curse of Doom)",
					new CanCastMoreBeforeSimulationEnds(CURSE_OF_DOOM)
			),
			testData(
					"Health < 100",
					new LessThan(new CasterHealth(), new Constant(100))
			),
			testData(
					"Mana < 100",
					new LessThan(new CasterMana(), new Constant(100))
			),
			testData(
					"Health% < 30",
					new LessThan(new CasterHealthPct(), new Constant(30))
			),
			testData(
					"Mana% < 30",
					new LessThan(new CasterManaPct(), new Constant(30))
			),
			testData(
					"Target.Health% < 30",
					new LessThan(new TargetHealthPct(), new Constant(30))
			),
			testData(
					"Target.Mana% < 30",
					new LessThan(new TargetManaPct(), new Constant(30))
			),
			testData(
					"FullDuration < 30",
					new LessThan(
							new FullDuration(),
							new Constant(30)
					)
			),
			testData(
					"RemainingCooldown(Curse of Doom) < 30",
					new LessThan(
							new RemainingCooldown(CURSE_OF_DOOM),
							new Constant(30)
					)
			),
			testData(
					"Mana < 100",
					new LessThan(new CasterMana(), new Constant(100))
			),
			testData(
					"Mana <= 100",
					new LessThanOrEqual(new CasterMana(), new Constant(100))
			),
			testData(
					"Mana > 100",
					new GreaterThan(new CasterMana(), new Constant(100))
			),
			testData(
					"Mana >= 100",
					new GreaterThanOrEqual(new CasterMana(), new Constant(100))
			),
			testData(
					"~Target.HasEffect(Curse of *) & FullDuration <= RemainingCooldown(Curse of Doom)",
					new And(
							new Not(new TargetHasEffect("Curse of *")),
							new LessThanOrEqual(
									new FullDuration(),
									new RemainingCooldown(CURSE_OF_DOOM)
							)
					)
			),
			testData(
					"~(Target.HasEffect(Curse of *) | FullDuration > RemainingCooldown(Curse of Doom))",
					new Not(
							new Or(
									new TargetHasEffect("Curse of *"),
									new GreaterThan(
											new FullDuration(),
											new RemainingCooldown(CURSE_OF_DOOM)
									)
							)
					)
			)
	);

	record TestData(String string, ScriptCommandCondition condition) {}

	static TestData testData(String string, ScriptCommandCondition condition) {
		return new TestData(string, condition);
	}
}