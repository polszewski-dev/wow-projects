package wow.commons.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeScaling;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.ConditionOperator;
import wow.commons.model.talent.TalentTree;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.attribute.condition.MiscCondition.PHYSICAL;
import static wow.commons.model.attribute.condition.MiscCondition.SPELL;

/**
 * User: POlszewski
 * Date: 2025-03-05
 */
class AttributesParserTest {
	@ParameterizedTest
	@MethodSource("testData")
	void parse(TestData testData) {
		var result = AttributesParser.parse(testData.string);

		assertThat(result).isEqualTo(testData.attributes);
	}

	@ParameterizedTest
	@MethodSource("testData")
	void parseWithoutValue(TestData testData) {
		if (testData.attributes().list().size() > 1) {
			return;
		}

		var firstAttribute = testData.attributes.list().getFirst();
		var result = AttributesParser.parse(testData.stringWithoutValue(), firstAttribute.value());

		assertThat(result).isEqualTo(firstAttribute);
	}

	static Stream<TestData> testData() {
		return TEST_DATA.stream();
	}

	record TestData(String string, Attributes attributes) {
		TestData(String string, Attribute... attributes) {
			this(string, Attributes.of(attributes));
		}

		String stringWithoutValue() {
			return string.substring(string.indexOf(' ') + 1);
		}
	}

	static final List<TestData> TEST_DATA = List.of(
			new TestData("10 Power", Attribute.of(POWER, 10)),
			new TestData("-10 Power", Attribute.of(POWER, -10)),
			new TestData("0.5 Power", Attribute.of(POWER, 0.5)),
			new TestData("-0.5 Power", Attribute.of(POWER, -0.5)),
			new TestData("10 Power%", Attribute.of(POWER_PCT, 10)),
			new TestData("10 Party.Power", Attribute.of(PARTY_POWER, 10)),
			new TestData("10 Power [Spell]", Attribute.of(POWER, 10, SPELL)),
			new TestData("10 Power [Spell | Physical]", Attribute.of(POWER, 10, ConditionOperator.or(SPELL, PHYSICAL))),
			new TestData("10 * level Power [Spell]", Attribute.of(POWER, 10, SPELL, AttributeScaling.LEVEL)),
			new TestData(
					"10 * level Power [Spell] + 20 Power [Spell]",
					Attribute.of(POWER, 10, SPELL, AttributeScaling.LEVEL),
					Attribute.of(POWER, 20, SPELL)
			),
			new TestData(
					"10 * numEffectsOnTarget(Affliction, 60) Power [Spell]",
					 Attribute.of(POWER, 10, SPELL, new AttributeScaling.NumberOfEffectsOnTarget(TalentTree.AFFLICTION, Percent.of(60)))
			)
	);
}