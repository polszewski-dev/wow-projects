package wow.commons.model.attribute.condition;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.condition.AttributeConditionFormatter.formatCondition;
import static wow.commons.model.spell.SpellSchool.*;

/**
 * User: POlszewski
 * Date: 2023-10-15
 */
class AttributeConditionTest {
	@Test
	void and() {
		var condition = ConditionOperator.and(
				MiscCondition.HEALING,
				MiscCondition.DIRECT
		);

		assertCondition(condition, "Healing & Direct");
	}

	@Test
	void or() {
		var condition = ConditionOperator.or(
				AttributeCondition.of(SHADOW),
				AttributeCondition.of(FROST)
		);

		assertCondition(condition, "Shadow | Frost");
	}

	@Test
	void or3() {
		var condition = ConditionOperator.or(
				AttributeCondition.of(SHADOW),
				ConditionOperator.or(AttributeCondition.of(FROST), AttributeCondition.of(FIRE))
		);

		assertCondition(condition, "Shadow | Frost | Fire");
	}

	void assertCondition(AttributeCondition condition, String expected) {
		assertThat(formatCondition(condition)).isEqualTo(expected);
	}
}