package wow.minmax.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wow.commons.model.Percent;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.AttributeCondition;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static wow.commons.model.attribute.primitive.PrimitiveAttributeId.*;
import static wow.commons.model.spell.SpellSchool.*;

/**
 * User: POlszewski
 * Date: 2022-11-09
 */
class AttributesDiffFinderTest {
	@DisplayName("Difference between primitive attributes")
	@ParameterizedTest(name = "[{index}] {0} - {1} = {2}")
	@MethodSource
	void testPrimitiveAttributeDifference(Attributes attributes1, Attributes attributes2, Attributes expectedResult) {
		AttributesDiff diff = new AttributesDiffFinder(
				attributes1, attributes2
		).getDiff();

		assertThat(diff.getAttributes()).hasToString(expectedResult.toString());
	}

	static Stream<Arguments> testPrimitiveAttributeDifference() {
		return Stream.of(
				arguments(
						Attributes.of(SPELL_DAMAGE, 10),
						Attributes.EMPTY,
						Attributes.of(SPELL_DAMAGE, 10)
				),
				arguments(
						Attributes.EMPTY,
						Attributes.of(SPELL_DAMAGE, 10),
						Attributes.of(SPELL_DAMAGE, -10)
				),
				arguments(
						Attributes.of(SPELL_DAMAGE, 10),
						Attributes.of(SPELL_DAMAGE, 10),
						Attributes.EMPTY
				),
				arguments(
						Attributes.of(SPELL_DAMAGE, 10),
						Attributes.of(SPELL_DAMAGE, 7),
						Attributes.of(SPELL_DAMAGE, 3)
				),
				arguments(
						Attributes.of(
								Attribute.of(SPELL_DAMAGE, 10, AttributeCondition.of(FIRE)),
								Attribute.of(SPELL_DAMAGE, 20),
								Attribute.of(SPELL_DAMAGE, 30)
						),
						Attributes.of(
								Attribute.of(SPELL_DAMAGE, 4),
								Attribute.of(SPELL_DAMAGE, 6),
								Attribute.of(SPELL_DAMAGE, 8, AttributeCondition.of(FIRE))
						),
						Attributes.of(
								Attribute.of(SPELL_DAMAGE, 40),
								Attribute.of(SPELL_DAMAGE, 2, AttributeCondition.of(FIRE))
						)
				),
				arguments(
						Attributes.of(
								Attribute.of(SPELL_DAMAGE, 10),
								Attribute.of(SPELL_CRIT_RATING, 15),
								Attribute.of(SPELL_DAMAGE, 20)
						),
						Attributes.of(
								Attribute.of(SPELL_DAMAGE, 5),
								Attribute.of(SPELL_CRIT_RATING, 6)
						),
						Attributes.of(
								Attribute.of(SPELL_DAMAGE, 25),
								Attribute.of(SPELL_CRIT_RATING, 9)
						)
				),
				arguments(
						Attributes.of(
								Attribute.of(SPELL_DAMAGE, 10),
								Attribute.of(SPELL_CRIT_RATING, 15, AttributeCondition.of(FIRE)),
								Attribute.of(SPELL_DAMAGE, 20, AttributeCondition.of(ARCANE))
						),
						Attributes.of(
								Attribute.of(SPELL_DAMAGE, 5),
								Attribute.of(SPELL_CRIT_RATING, 6, AttributeCondition.of(FROST))
						),
						Attributes.of(
								Attribute.of(SPELL_DAMAGE, 5),
								Attribute.of(SPELL_DAMAGE, 20, AttributeCondition.of(ARCANE)),
								Attribute.of(SPELL_CRIT_RATING, 15, AttributeCondition.of(FIRE)),
								Attribute.of(SPELL_CRIT_RATING, -6, AttributeCondition.of(FROST))
						)
				),

				arguments(
						Attributes.of(SPELL_HIT_PCT, Percent.of(10)),
						Attributes.EMPTY,
						Attributes.of(SPELL_HIT_PCT, Percent.of(10))
				),
				arguments(
						Attributes.EMPTY,
						Attributes.of(SPELL_HIT_PCT, Percent.of(10)),
						Attributes.of(SPELL_HIT_PCT, Percent.of(-10))
				),
				arguments(
						Attributes.of(SPELL_HIT_PCT, Percent.of(10)),
						Attributes.of(SPELL_HIT_PCT, Percent.of(10)),
						Attributes.EMPTY
				),
				arguments(
						Attributes.of(SPELL_HIT_PCT, Percent.of(10)),
						Attributes.of(SPELL_HIT_PCT, Percent.of(7)),
						Attributes.of(SPELL_HIT_PCT, Percent.of(3))
				),
				arguments(
						Attributes.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(10), AttributeCondition.of(FIRE)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(20)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(30))
						),
						Attributes.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(4)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(6)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(8), AttributeCondition.of(FIRE))
						),
						Attributes.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(40)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(2), AttributeCondition.of(FIRE))
						)
				),
				arguments(
						Attributes.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(10)),
								Attribute.of(SPELL_CRIT_PCT, Percent.of(15)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(20))
						),
						Attributes.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(5)),
								Attribute.of(SPELL_CRIT_PCT, Percent.of(6))
						),
						Attributes.of(
								Attribute.of(SPELL_CRIT_PCT, Percent.of(9)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(25))
								)
				),
				arguments(
						Attributes.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(10)),
								Attribute.of(SPELL_CRIT_PCT, Percent.of(15), AttributeCondition.of(FIRE)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(20), AttributeCondition.of(ARCANE))
						),
						Attributes.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(5)),
								Attribute.of(SPELL_CRIT_PCT, Percent.of(6), AttributeCondition.of(FROST))
						),
						Attributes.of(
								Attribute.of(SPELL_CRIT_PCT, Percent.of(15), AttributeCondition.of(FIRE)),
								Attribute.of(SPELL_CRIT_PCT, Percent.of(-6), AttributeCondition.of(FROST)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(5)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(20), AttributeCondition.of(ARCANE))
						)
				)
		);
	}
}