package wow.commons.model.attribute.condition;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static wow.commons.model.attribute.condition.ConditionOperator.*;

/**
 * User: POlszewski
 * Date: 2025-10-05
 */
public class AttributeConditionFormatter {
	public static String format(AttributeCondition condition) {
		if (condition instanceof Or or) {
			return formatBinaryOperator(" | ", or);
		}
		if (condition instanceof And and) {
			return formatBinaryOperator(" & ", and);
		}
		if (condition instanceof Comma comma) {
			return formatComma(comma);
		}
		return condition.toString();
	}

	private static String formatBinaryOperator(String operatorStr, BinaryConditionOperator operator) {
		int priority = getPriority(operator);
		int leftPriority = getPriority(operator.left());
		int rightPriority = getPriority(operator.right());

		var leftStr = format(operator.left());
		var rightStr = format(operator.right());

		if (leftPriority > priority) {
			leftStr = "(" + leftStr + ")";
		}
		if (rightPriority > priority) {
			rightStr = "(" + rightStr + ")";
		}

		return leftStr + operatorStr +  rightStr;
	}

	private static String formatComma(Comma comma) {
		return comma.conditions().stream()
				.map(Object::toString)
				.collect(joining(", "));
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
