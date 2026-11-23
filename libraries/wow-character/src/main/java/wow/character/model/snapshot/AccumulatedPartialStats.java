package wow.character.model.snapshot;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.AttributeScalingParams;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.component.StatConversion;
import wow.commons.model.spell.ActionType;

import static wow.commons.model.attribute.AttributeCondition.*;

/**
 * User: POlszewski
 * Date: 2023-11-16
 */
public abstract class AccumulatedPartialStats extends AccumulatedStats implements AttributeScalingParams {
	public void accumulateConvertedAttribute(StatConversion conversion, double valueFrom) {
		var to = conversion.to();
		var ratio = conversion.ratioPct().value() / 100;
		var valueTo = valueFrom * ratio;

		accumulateAttribute(to, valueTo);
	}

	protected double getValue(Attribute attribute, double scaleFactor) {
		if (!checkCondition(attribute.condition())) {
			return 0;
		}

		return scaleFactor * attribute.getScaledValue(this);
	}

	protected abstract boolean checkCondition(AttributeCondition condition);

	protected boolean checkConditionOperator(Operator operator) {
		return switch (operator) {
			case And(var left, var right) ->
					checkCondition(left) && checkCondition(right);

			case Or(var left, var right) ->
					checkCondition(left) || checkCondition(right);

			case Comma comma ->
					checkComma(comma);
		};
	}

	private boolean checkComma(Comma comma) {
		for (var condition : comma.conditions()) {
			if (checkCondition(condition)) {
				return true;
			}
		}
		return false;
	}

	protected static boolean unsupported(AttributeCondition condition) {
		throw new IllegalArgumentException(condition + "");
	}

	public abstract ActionType getActionType();

	public abstract PowerType getPowerType();
}
