package wow.commons.util.condition;

import wow.commons.model.Condition;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static wow.commons.util.FormatUtil.decimalPointOnlyIfNecessary;

/**
 * User: POlszewski
 * Date: 2025-10-05
 */
public abstract class ConditionFormatter<T extends Condition> {
	public String format(T condition) {
		if (isOr(condition)) {
			return formatBinaryOperator(" | ", condition);
		}
		if (isAnd(condition)) {
			return formatBinaryOperator(" & ", condition);
		}
		if (isComma(condition)) {
			return formatComma(condition);
		}
		if (isNot(condition)) {
			return formatNot(condition);
		}
		return formatPrimitiveCondition(condition);
	}

	private String formatBinaryOperator(String operatorStr, T operator) {
		int priority = getPriority(operator);
		int leftPriority = getPriority(getLeft(operator));
		int rightPriority = getPriority(getRight(operator));

		var leftStr = format(getLeft(operator));
		var rightStr = format(getRight(operator));

		if (leftPriority > priority) {
			leftStr = "(" + leftStr + ")";
		}
		if (rightPriority > priority) {
			rightStr = "(" + rightStr + ")";
		}

		return leftStr + operatorStr +  rightStr;
	}

	private String formatComma(T comma) {
		return getCommaConditions(comma).stream()
				.map(this::format)
				.collect(joining(", "));
	}

	private String formatNot(T operator) {
		int priority = getPriority(operator);
		int conditionPriority = getPriority(getNotCondition(operator));

		var conditionStr = format(getNotCondition(operator));

		if (conditionPriority > priority) {
			conditionStr = "(" + conditionStr + ")";
		}

		return "~" + conditionStr;
	}

	protected abstract String formatPrimitiveCondition(T condition);

	protected boolean isBinaryOperator(T condition) {
		return false;
	}

	protected boolean isOr(T condition) {
		return false;
	}

	protected boolean isAnd(T condition) {
		return false;
	}

	protected boolean isComma(T condition) {
		return false;
	}

	protected boolean isNot(T condition) {
		return false;
	}

	protected T getLeft(T operator) {
		throw new UnsupportedOperationException();
	}

	protected T getRight(T operator) {
		throw new UnsupportedOperationException();
	}

	protected List<T> getCommaConditions(T comma) {
		throw new UnsupportedOperationException();
	}

	protected T getNotCondition(T operator) {
		throw new UnsupportedOperationException();
	}

	private int getPriority(T condition) {
		if (isOr(condition)) {
			return 3;
		}
		if (isAnd(condition)) {
			return 2;
		}
		if (isComma(condition)) {
			return 1;
		}
		return 0;
	}

	private Stream<T> getLeaves(T left, T right) {
		return Stream.concat(
				getLeaves(left), getLeaves(right)
		);
	}

	private Stream<T> getLeaves(T left) {
		if (isBinaryOperator(left)) {
			var childLeft = getLeft(left);
			var childRight = getRight(left);
			return getLeaves(childLeft, childRight);
		} else {
			return Stream.of(left);
		}
	}

	protected String formatFunction(String name, Object arg) {
		return name + "(" + arg + ")";
	}

	protected String formatOperator(String left, String operator, double right) {
		return left + " " + operator + " " + decimalPointOnlyIfNecessary(right);
	}
}
