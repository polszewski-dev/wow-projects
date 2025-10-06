package wow.commons.model.effect.component;

import wow.commons.model.Condition;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.ActionType;
import wow.commons.util.condition.ConditionCache;

import java.util.Objects;
import java.util.function.Function;

import static wow.commons.model.effect.component.StatConversionConditionCache.getCachedValue;
import static wow.commons.util.condition.StatConversionConditionParser.parseCondition;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public sealed interface StatConversionCondition extends Condition {
	EmptyCondition EMPTY = new EmptyCondition();

	static StatConversionCondition of(ActionType actionType) {
		return getCachedValue(actionType, ActionTypeCondition::new);
	}

	static StatConversionCondition of(PowerType powerType) {
		return getCachedValue(powerType, PowerTypeCondition::new);
	}

	static StatConversionCondition parse(String value) {
		return getCachedValue(
				value,
				x -> parseCondition(value)
		);
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

class StatConversionConditionCache extends ConditionCache<StatConversionCondition> {
	private static final StatConversionConditionCache INSTANCE = new StatConversionConditionCache();

	static <K> StatConversionCondition getCachedValue(K key, Function<K, StatConversionCondition> conditionMapper) {
		return INSTANCE.getValue(key, conditionMapper);
	}

	@Override
	protected StatConversionCondition emptyCondition() {
		return StatConversionCondition.EMPTY;
	}
}