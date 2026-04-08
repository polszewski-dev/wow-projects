package wow.commons.util;

import wow.commons.model.config.TimeRestricted;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-06-26
 */
public class GameVersionMap<K, V> extends TimeMap<K, V, GameVersionId> {
	public GameVersionMap() {
		super(GameVersionId.class);
	}

	public Optional<V> getOptional(PhaseId phaseId, K key) {
		return getOptional(phaseId.getGameVersionId(), key);
	}

	public static <K, V extends TimeRestricted> void putForGameVersion(GameVersionMap<K, V> map, K id, V value) {
		var gameVersionId = value.getGameVersionId();
		map.put(gameVersionId, id, value);
	}

	public static <K, V extends TimeRestricted> void addEntryForGameVersion(GameVersionMap<K, List<V>> map, K key, V value) {
		var gameVersionId = value.getGameVersionId();
		map.computeIfAbsent(gameVersionId, key, x -> new ArrayList<>()).add(value);
	}
}
