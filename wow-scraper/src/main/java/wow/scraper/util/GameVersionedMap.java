package wow.scraper.util;

import wow.commons.model.pve.GameVersionId;

import java.util.*;
import java.util.function.Function;

/**
 * User: POlszewski
 * Date: 2023-06-26
 */
public class GameVersionedMap<K, V> {
	private final Map<GameVersionId, Map<K, V>> map = new EnumMap<>(GameVersionId.class);

	public void put(GameVersionId gameVersion, K key, V value) {
		map.computeIfAbsent(gameVersion, x -> new HashMap<>()).put(key, value);
	}

	public Optional<V> getOptional(GameVersionId gameVersion, K key) {
		return Optional.ofNullable(map.getOrDefault(gameVersion, Map.of()).get(key));
	}

	public Set<K> keySet(GameVersionId gameVersion) {
		return map.getOrDefault(gameVersion, Map.of()).keySet();
	}

	public Collection<V> values(GameVersionId gameVersion) {
		return map.getOrDefault(gameVersion, Map.of()).values();
	}

	public List<V> allValues() {
		return map.values().stream().flatMap(x -> x.values().stream()).toList();
	}

	public boolean containsKey(GameVersionId gameVersion) {
		return map.containsKey(gameVersion);
	}

	public boolean containsKey(GameVersionId gameVersion, K key) {
		return map.getOrDefault(gameVersion, Map.of()).containsKey(key);
	}

	public V computeIfAbsent(GameVersionId gameVersion, K key, Function<K, V> mappingFunction) {
		return map
				.computeIfAbsent(gameVersion, x -> new HashMap<>())
				.computeIfAbsent(key, mappingFunction);
	}
}
