package wow.commons.util;

import java.util.*;
import java.util.function.Function;

/**
 * User: POlszewski
 * Date: 2023-12-01
 */
public abstract class TimeMap<K, V, T extends Enum<T>> {
	protected final Map<T, Map<K, V>> map;

	protected TimeMap(Class<T> tClass) {
		this.map = new EnumMap<>(tClass);
	}

	public void put(T timeKey, K key, V value) {
		getMap(timeKey).put(key, value);
	}

	private Map<K, V> getMap(T timeKey) {
		return map.computeIfAbsent(timeKey, x -> new LinkedHashMap<>());
	}

	public Optional<V> getOptional(T timeKey, K key) {
		V value = map.getOrDefault(timeKey, Map.of()).get(key);
		return Optional.ofNullable(value);
	}

	public V computeIfAbsent(T timeKey, K key, Function<K, V> mappingFunction) {
		return getMap(timeKey).computeIfAbsent(key, mappingFunction);
	}

	public Set<K> keySet(T timeKey) {
		return map.getOrDefault(timeKey, Map.of()).keySet();
	}

	public Collection<V> values(T timeKey) {
		return map.getOrDefault(timeKey, Map.of()).values();
	}

	public List<V> allValues() {
		return map.values().stream().flatMap(x -> x.values().stream()).distinct().toList();
	}

	public boolean containsKey(T timeKey) {
		return map.containsKey(timeKey);
	}

	public boolean containsKey(T timeKey, K key) {
		return map.getOrDefault(timeKey, Map.of()).containsKey(key);
	}
}
