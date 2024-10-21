package wow.commons.model.attribute.condition;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;

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

	static Comma comma(AttributeCondition left, AttributeCondition right) {
		return new Comma(left, right);
	}

	static Or or(AttributeCondition... conditions) {
		return join(conditions, ConditionOperator::or);
	}

	static And and(AttributeCondition... conditions) {
		return join(conditions, ConditionOperator::and);
	}

	static Comma comma(AttributeCondition... conditions) {
		return join(conditions, ConditionOperator::comma);
	}

	private static <T extends AttributeCondition> T join(AttributeCondition[] conditions, BiFunction<AttributeCondition, AttributeCondition, T> operator) {
		if (conditions.length < 2) {
			throw new IllegalArgumentException();
		}

		var result = conditions[conditions.length - 1];

		for (int i = conditions.length - 2; i >= 0; --i) {
			result = operator.apply(conditions[i], result);
		}

		return (T) result;
	}

	sealed interface BinaryConditionOperator extends ConditionOperator {
		AttributeCondition left();
		AttributeCondition right();

		default List<AttributeCondition> getLeaves() {
			return ConditionOperator.getLeaves(left(), right()).toList();
		}
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

	record Comma(AttributeCondition left, AttributeCondition right) implements BinaryConditionOperator {
		public Comma {
			Objects.requireNonNull(left);
			Objects.requireNonNull(right);

			var leaves = ConditionOperator.getLeaves(left, right).toList();

			if (leaves.stream().anyMatch(ConditionOperator.class::isInstance)) {
				throw new IllegalArgumentException("Only simple types");
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
			return binaryOperatorToString(", ", comma);
		}
		return condition.toString();
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
