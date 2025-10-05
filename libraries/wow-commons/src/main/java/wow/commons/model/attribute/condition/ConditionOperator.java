package wow.commons.model.attribute.condition;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

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
			return conditionToString(this);
		}
	}

	record And(AttributeCondition left, AttributeCondition right) implements BinaryConditionOperator {
		public And {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);
		}

		@Override
		public String toString() {
			return conditionToString(this);
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
			return conditionToString(this);
		}
	}

	private static String conditionToString(AttributeCondition condition) {
		if (condition instanceof Or or) {
			return binaryOperatorToString(" | ", or);
		}
		if (condition instanceof And and) {
			return binaryOperatorToString(" & ", and);
		}
		if (condition instanceof Comma comma) {
			return formatComma(comma);
		}
		return condition.toString();
	}

	private static String formatComma(Comma comma) {
		return comma.conditions().stream()
				.map(Object::toString)
				.collect(joining(", "));
	}

	private static String binaryOperatorToString(String operatorStr, BinaryConditionOperator operator) {
		int priority = getPriority(operator);
		int leftPriority = getPriority(operator.left());
		int rightPriority = getPriority(operator.right());

		var leftStr = conditionToString(operator.left());
		var rightStr = conditionToString(operator.right());

		if (leftPriority > priority) {
			leftStr = "(" + leftStr + ")";
		}
		if (rightPriority > priority) {
			rightStr = "(" + rightStr + ")";
		}

		return leftStr + operatorStr +  rightStr;
	}

	private static int getPriority(AttributeCondition condition) {
		if (condition instanceof Or) {
			return 3;
		}
		if (condition instanceof And) {
			return 2;
		}
		if (condition instanceof Comma) {
			return 1;
		}
		return 0;
	}

	private static Stream<AttributeCondition> getLeaves(AttributeCondition left, AttributeCondition right) {
		return Stream.concat(
				getLeaves(left), getLeaves(right)
		);
	}

	private static Stream<AttributeCondition> getLeaves(AttributeCondition left) {
		if (left instanceof BinaryConditionOperator c) {
			return getLeaves(c.left(), c.right());
		} else {
			return Stream.of(left);
		}
	}
}
