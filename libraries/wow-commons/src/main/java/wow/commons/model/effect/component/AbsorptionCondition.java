package wow.commons.model.effect.component;

import wow.commons.model.Condition;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.SpellSchool;
import wow.commons.util.condition.ConditionCache;

import java.util.Objects;
import java.util.function.Function;

import static wow.commons.model.effect.component.AbsorptionConditionCache.getCachedValue;
import static wow.commons.util.condition.AbsorptionConditionParser.parseCondition;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public sealed interface AbsorptionCondition extends Condition {
	EmptyCondition EMPTY = new EmptyCondition();

	static AbsorptionCondition of(ActionType actionType) {
		return getCachedValue(actionType, ActionTypeCondition::new);
	}

	static AbsorptionCondition of(PowerType powerType) {
		return getCachedValue(powerType, PowerTypeCondition::new);
	}

	static AbsorptionCondition of(SpellSchool spellSchool) {
		return getCachedValue(spellSchool, SpellSchoolCondition::new);
	}

	static AbsorptionCondition parse(String value) {
		return getCachedValue(
				value,
				x -> parseCondition(value)
		);
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

class AbsorptionConditionCache extends ConditionCache<AbsorptionCondition> {
	private static final AbsorptionConditionCache INSTANCE = new AbsorptionConditionCache();

	static <K> AbsorptionCondition getCachedValue(K key, Function<K, AbsorptionCondition> conditionMapper) {
		return INSTANCE.getValue(key, conditionMapper);
	}

	@Override
	protected AbsorptionCondition emptyCondition() {
		return AbsorptionCondition.EMPTY;
	}
}
