package wow.commons.model.effect.component;

import wow.commons.model.Condition;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.ActionType;

import java.util.Objects;

import static wow.commons.util.condition.StatConversionConditionParser.parseCondition;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public sealed interface StatConversionCondition extends Condition {
	EmptyCondition EMPTY = new EmptyCondition();

	static StatConversionCondition of(ActionType actionType) {
		return new ActionTypeCondition(actionType);
	}

	static StatConversionCondition of(PowerType powerType) {
		return new PowerTypeCondition(powerType);
	}

	static StatConversionCondition parse(String value) {
		return parseCondition(value);
	}

	record EmptyCondition() implements StatConversionCondition {
		@Override
		public boolean isEmpty() {
			return true;
		}
	}

	record ActionTypeCondition(ActionType actionType) implements StatConversionCondition {
		public ActionTypeCondition {
			Objects.requireNonNull(actionType);
		}
	}

	record PowerTypeCondition(PowerType powerType) implements StatConversionCondition {
		public PowerTypeCondition {
			Objects.requireNonNull(powerType);
		}
	}
}
