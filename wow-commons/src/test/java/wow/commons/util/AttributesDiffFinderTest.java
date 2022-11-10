package wow.commons.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.spells.SpellSchool;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;

/**
 * User: POlszewski
 * Date: 2022-11-09
 */
class AttributesDiffFinderTest {
	@DisplayName("Difference between primitive attributes")
	@ParameterizedTest(name = "[{index}] {0} - {1} = {2}")
	@MethodSource
	void testPrimitiveAttributeDifference(List<PrimitiveAttribute> attributeList1, List<PrimitiveAttribute> attributeList2, List<PrimitiveAttribute> expectedResult) {
		AttributesDiff diff = new AttributesDiffFinder(
				Attributes.of(attributeList1),
				Attributes.of(attributeList2)
		).getDiff();

		assertAll(
				() -> assertThat(diff.getAttributes().getPrimitiveAttributeList()).isEqualTo(expectedResult),
				() -> assertThat(diff.getAddedAbilities()).isEmpty(),
				() -> assertThat(diff.getRemovedAbilities()).isEmpty()
		);
	}

	static Stream<Arguments> testPrimitiveAttributeDifference() {
		return Stream.of(
				arguments(
						List.of(
								Attribute.of(SPELL_DAMAGE, 10)
						),
						List.of(),
						List.of(
								Attribute.of(SPELL_DAMAGE, 10)
						)
				),
				arguments(
						List.of(),
						List.of(
								Attribute.of(SPELL_DAMAGE, 10)
						),
						List.of(
								Attribute.of(SPELL_DAMAGE, -10)
						)
				),
				arguments(
						List.of(
								Attribute.of(SPELL_DAMAGE, 10)
						),
						List.of(
								Attribute.of(SPELL_DAMAGE, 10)
						),
						List.of()
				),
				arguments(
						List.of(
								Attribute.of(SPELL_DAMAGE, 10)
						),
						List.of(
								Attribute.of(SPELL_DAMAGE, 7)
						),
						List.of(
								Attribute.of(SPELL_DAMAGE, 3)
						)
				),
				arguments(
						List.of(
								Attribute.of(SPELL_DAMAGE, 10, AttributeCondition.of(SpellSchool.FIRE)),
								Attribute.of(SPELL_DAMAGE, 20),
								Attribute.of(SPELL_DAMAGE, 30)
						),
						List.of(
								Attribute.of(SPELL_DAMAGE, 4),
								Attribute.of(SPELL_DAMAGE, 6),
								Attribute.of(SPELL_DAMAGE, 8, AttributeCondition.of(SpellSchool.FIRE))
						),
						List.of(
								Attribute.of(SPELL_DAMAGE, 40),
								Attribute.of(SPELL_DAMAGE, 2, AttributeCondition.of(SpellSchool.FIRE))
						)
				),
				arguments(
						List.of(
								Attribute.of(SPELL_DAMAGE, 10),
								Attribute.of(SPELL_CRIT_RATING, 15),
								Attribute.of(SPELL_DAMAGE, 20)
						),
						List.of(
								Attribute.of(SPELL_DAMAGE, 5),
								Attribute.of(SPELL_CRIT_RATING, 6)
						),
						List.of(
								Attribute.of(SPELL_DAMAGE, 25),
								Attribute.of(SPELL_CRIT_RATING, 9)
						)
				),
				arguments(
						List.of(
								Attribute.of(SPELL_DAMAGE, 10),
								Attribute.of(SPELL_CRIT_RATING, 15, AttributeCondition.of(SpellSchool.FIRE)),
								Attribute.of(SPELL_DAMAGE, 20, AttributeCondition.of(SpellSchool.ARCANE))
						),
						List.of(
								Attribute.of(SPELL_DAMAGE, 5),
								Attribute.of(SPELL_CRIT_RATING, 6, AttributeCondition.of(SpellSchool.FROST))
						),
						List.of(
								Attribute.of(SPELL_DAMAGE, 5),
								Attribute.of(SPELL_DAMAGE, 20, AttributeCondition.of(SpellSchool.ARCANE)),
								Attribute.of(SPELL_CRIT_RATING, 15, AttributeCondition.of(SpellSchool.FIRE)),
								Attribute.of(SPELL_CRIT_RATING, -6, AttributeCondition.of(SpellSchool.FROST))
						)
				),

				arguments(
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(10))
						),
						List.of(),
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(10))
						)
				),
				arguments(
						List.of(),
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(10))
						),
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(-10))
						)
				),
				arguments(
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(10))
						),
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(10))
						),
						List.of()
				),
				arguments(
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(10))
						),
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(7))
						),
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(3))
						)
				),
				arguments(
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(10), AttributeCondition.of(SpellSchool.FIRE)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(20)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(30))
						),
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(4)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(6)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(8), AttributeCondition.of(SpellSchool.FIRE))
						),
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(40)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(2), AttributeCondition.of(SpellSchool.FIRE))
						)
				),
				arguments(
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(10)),
								Attribute.of(SPELL_CRIT_PCT, Percent.of(15)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(20))
						),
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(5)),
								Attribute.of(SPELL_CRIT_PCT, Percent.of(6))
						),
						List.of(
								Attribute.of(SPELL_CRIT_PCT, Percent.of(9)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(25))
								)
				),
				arguments(
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(10)),
								Attribute.of(SPELL_CRIT_PCT, Percent.of(15), AttributeCondition.of(SpellSchool.FIRE)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(20), AttributeCondition.of(SpellSchool.ARCANE))
						),
						List.of(
								Attribute.of(SPELL_HIT_PCT, Percent.of(5)),
								Attribute.of(SPELL_CRIT_PCT, Percent.of(6), AttributeCondition.of(SpellSchool.FROST))
						),
						List.of(
								Attribute.of(SPELL_CRIT_PCT, Percent.of(15), AttributeCondition.of(SpellSchool.FIRE)),
								Attribute.of(SPELL_CRIT_PCT, Percent.of(-6), AttributeCondition.of(SpellSchool.FROST)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(5)),
								Attribute.of(SPELL_HIT_PCT, Percent.of(20), AttributeCondition.of(SpellSchool.ARCANE))
						)
				)
		);
	}
}