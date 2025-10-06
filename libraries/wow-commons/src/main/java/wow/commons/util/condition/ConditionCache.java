package wow.commons.util.condition;

import wow.commons.model.Condition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public abstract class ConditionCache<T extends Condition> {
	private final Map<Object, T> map = new HashMap<>();

	public <K> T getValue(K key, Function<K, T> conditionMapper) {
		if (key == null) {
			return emptyCondition();
		}

		var cachedValue = map.get(key);

		if (cachedValue != null) {
			return cachedValue;
		}

		var newValue = conditionMapper.apply(key);

		map.put(key, newValue);
		return newValue;
	}

	protected abstract T emptyCondition();
}
