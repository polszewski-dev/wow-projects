package wow.commons.model.attribute.condition;

import java.util.List;
import java.util.Objects;

import static wow.commons.model.attribute.condition.AttributeConditionFormatter.format;

/**
 * User: POlszewski
 * Date: 2023-10-14
 */
public sealed interface ConditionOperator extends AttributeCondition {
	static Or or(AttributeCondition left, AttributeCondition right) {
		return new Or(left, right);
	}

	static And and(AttributeCondition left, AttributeCondition right) {
		return new And(left, right);
	}

	static Comma comma(AttributeCondition... conditions) {
		return new Comma(List.of(conditions));
	}

	static Comma comma(List<AttributeCondition> conditions) {
		return new Comma(List.copyOf(conditions));
	}

	sealed interface BinaryConditionOperator extends ConditionOperator {
		AttributeCondition left();

		AttributeCondition right();
	}

	record Or(AttributeCondition left, AttributeCondition right) implements BinaryConditionOperator {
		public Or {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);
		}

		@Override
		public String toString() {
			return format(this);
		}
	}

	record And(AttributeCondition left, AttributeCondition right) implements BinaryConditionOperator {
		public And {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);
		}

		@Override
		public String toString() {
			return format(this);
		}
	}

	record Comma(List<AttributeCondition> conditions) implements ConditionOperator {
		public Comma {
			Objects.requireNonNull(conditions);

			if (conditions.size() < 2) {
				throw new IllegalArgumentException("At least 2 conditions required");
			}

			if (conditions.stream().anyMatch(ConditionOperator.class::isInstance)) {
				throw new IllegalArgumentException("Only simple types");
			}

			if (conditions.stream().map(AttributeCondition::getClass).distinct().count() > 1) {
				throw new IllegalArgumentException("All conditions must be of the same type");
			}
		}

		@Override
		public String toString() {
			return format(this);
		}
	}
}
