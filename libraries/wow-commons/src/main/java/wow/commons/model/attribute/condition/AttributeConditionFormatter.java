package wow.commons.model.attribute.condition;

import wow.commons.util.condition.ConditionFormatter;

import java.util.List;

import static wow.commons.model.attribute.condition.ConditionOperator.*;

/**
 * User: POlszewski
 * Date: 2025-10-05
 */
public class AttributeConditionFormatter extends ConditionFormatter<AttributeCondition> {
	public static String formatCondition(AttributeCondition condition) {
		return new AttributeConditionFormatter().format(condition);
	}

	@Override
	protected String formatPrimitiveCondition(AttributeCondition condition) {
		return condition.toString();
	}

	@Override
	protected boolean isBinaryOperator(AttributeCondition condition) {
		return condition instanceof BinaryConditionOperator;
	}

	@Override
	protected boolean isOr(AttributeCondition condition) {
		return condition instanceof Or;
	}

	@Override
	protected boolean isAnd(AttributeCondition condition) {
		return condition instanceof And;
	}

	@Override
	protected boolean isComma(AttributeCondition condition) {
		return condition instanceof Comma;
	}

	@Override
	protected AttributeCondition getLeft(AttributeCondition operator) {
		return ((BinaryConditionOperator) operator).left();
	}

	@Override
	protected AttributeCondition getRight(AttributeCondition operator) {
		return ((BinaryConditionOperator) operator).right();
	}

	@Override
	protected List<AttributeCondition> getCommaConditions(AttributeCondition comma) {
		return ((Comma) comma).conditions();
	}
}
