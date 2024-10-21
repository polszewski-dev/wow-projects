package wow.commons.model.attribute.condition;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
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

		assertThat(condition).hasToString("Healing & Direct");
	}

	@Test
	void or() {
		var condition = ConditionOperator.or(
				AttributeCondition.of(SHADOW),
				AttributeCondition.of(FROST)
		);

		assertThat(condition).hasToString("Shadow | Frost");
	}

	@Test
	void or3() {
		var condition = ConditionOperator.or(
				AttributeCondition.of(SHADOW),
				ConditionOperator.or(AttributeCondition.of(FROST), AttributeCondition.of(FIRE))
		);

		assertThat(condition).hasToString("Shadow | Frost | Fire");
	}
}