package wow.commons.util;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2023-12-01
 */
public abstract class TimeMap<K, V, T extends Enum<T>> {
	protected abstract static class TimeEntryMap<T extends Enum<T>, V> {
		public abstract V get(T timeKey);

		public abstract void put(T timeKey, V value);

		public boolean containsKey(T timeKey) {
			return get(timeKey) != null;
		}

		public V computeIfAbsent(T timeKey, Supplier<V> supplier) {
			if (containsKey(timeKey)) {
				return get(timeKey);
			}

			var newValue = supplier.get();

			put(timeKey, newValue);

			return newValue;
		}

		public List<V> values() {
			var result = new ArrayList<V>();

			for (var timeKey : timeKeyValues()) {
				if (containsKey(timeKey)) {
					result.add(get(timeKey));
				}
			}

			return result;
		}

		protected abstract T[] timeKeyValues();

		private static class EmptyTimeEntryMap<T extends Enum<T>, V> extends TimeEntryMap<T, V> {
			@Override
			public V get(T timeKey) {
				return null;
			}

			@Override
			public void put(T timeKey, V value) {
				throw new UnsupportedOperationException();
			}

			@Override
			public List<V> values() {
				return List.of();
			}

			@Override
			protected T[] timeKeyValues() {
				throw new UnsupportedOperationException();
			}
		}

		public static final TimeEntryMap<?, ?> EMPTY = new EmptyTimeEntryMap<>();
	}

	protected final Map<K, TimeEntryMap<T, V>> map = new LinkedHashMap<>();

	protected TimeMap() {}

	public void put(T timeKey, K key, V value) {
		getMap(key).put(timeKey, value);
	}

	private TimeEntryMap<T, V> getMap(K key) {
		return map.computeIfAbsent(key, x -> newTimeEntryMap());
	}

	public Optional<V> getOptional(T timeKey, K key) {
		var value = map.getOrDefault(key, emptyTimeEntryMap()).get(timeKey);

		return Optional.ofNullable(value);
	}

	public V getOrDefault(T timeKey, K key, V defaultValue) {
		var submap = map.getOrDefault(key, emptyTimeEntryMap());
		var value = submap.get(timeKey);

		return (value != null || submap.containsKey(timeKey)) ? value : defaultValue;
	}

	public V computeIfAbsent(T timeKey, K key, Supplier<V> supplier) {
		return getMap(key).computeIfAbsent(timeKey, supplier);
	}

	public Set<K> keySet(T timeKey) {
		return map.entrySet().stream()
				.filter(e -> e.getValue().containsKey(timeKey))
				.map(Map.Entry::getKey)
				.collect(Collectors.toUnmodifiableSet());
	}

	public List<V> values(T timeKey) {
		return map.values().stream()
				.filter(x -> x.containsKey(timeKey))
				.map(x -> x.get(timeKey))
				.toList();
	}

	public List<V> allValues() {
		return map.values().stream()
				.flatMap(x -> x.values().stream())
				.distinct()
				.toList();
	}

	public boolean containsKey(T timeKey) {
		return map.values().stream()
				.anyMatch(x -> x.containsKey(timeKey));
	}

	@SuppressWarnings("unchecked")
	private TimeEntryMap<T, V> emptyTimeEntryMap() {
		return (TimeEntryMap<T, V>) TimeEntryMap.EMPTY;
	}

	protected abstract TimeEntryMap<T, V> newTimeEntryMap();
}
