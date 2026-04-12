package wow.commons.model.effect.component;

import wow.commons.model.Condition;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.SpellSchool;

import java.util.Objects;

import static wow.commons.util.condition.AbsorptionConditionParser.parseCondition;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public sealed interface AbsorptionCondition extends Condition {
	EmptyCondition EMPTY = new EmptyCondition();

	static AbsorptionCondition of(ActionType actionType) {
		return new ActionTypeCondition(actionType);
	}

	static AbsorptionCondition of(PowerType powerType) {
		return new PowerTypeCondition(powerType);
	}

	static AbsorptionCondition of(SpellSchool spellSchool) {
		return new SpellSchoolCondition(spellSchool);
	}

	static AbsorptionCondition parse(String value) {
		return parseCondition(value);
	}

	record EmptyCondition() implements AbsorptionCondition {
		@Override
		public boolean isEmpty() {
			return true;
		}
	}

	record ActionTypeCondition(ActionType actionType) implements AbsorptionCondition {
		public ActionTypeCondition {
			Objects.requireNonNull(actionType);
		}
	}

	record PowerTypeCondition(PowerType powerType) implements AbsorptionCondition {
		public PowerTypeCondition {
			Objects.requireNonNull(powerType);
		}
	}

	record SpellSchoolCondition(SpellSchool spellSchool) implements AbsorptionCondition {
		public SpellSchoolCondition {
			Objects.requireNonNull(spellSchool);
		}
	}
}
