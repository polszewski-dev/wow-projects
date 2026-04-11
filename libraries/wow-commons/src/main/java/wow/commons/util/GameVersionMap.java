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
	public Optional<V> getOptional(PhaseId phaseId, K key) {
		return getOptional(phaseId.getGameVersionId(), key);
	}

	public static <K, V extends TimeRestricted> void putForGameVersion(GameVersionMap<K, V> map, K id, V value) {
		var gameVersionId = value.getGameVersionId();
		map.put(gameVersionId, id, value);
	}

	public static <K, V extends TimeRestricted> void addEntryForGameVersion(GameVersionMap<K, List<V>> map, K key, V value) {
		var gameVersionId = value.getGameVersionId();
		map.computeIfAbsent(gameVersionId, key, ArrayList::new).add(value);
	}

	private static class GameVersionEntryMap<V> extends TimeEntryMap<GameVersionId, V> {
		private V vanillaValue;
		private V tbcValue;
		private V wotlkValue;

		@Override
		public V get(GameVersionId timeKey) {
			return switch (timeKey) {
				case VANILLA -> vanillaValue;
				case TBC -> tbcValue;
				case WOTLK -> wotlkValue;
			};
		}

		@Override
		public void put(GameVersionId timeKey, V value) {
			switch (timeKey) {
				case VANILLA -> vanillaValue = value;
				case TBC -> tbcValue = value;
				case WOTLK -> wotlkValue = value;
			}
		}

		@Override
		protected GameVersionId[] timeKeyValues() {
			return GameVersionId.values();
		}

		@Override
		public void compactLists() {
			var compactedListMap = getCompactedListMap(vanillaValue, tbcValue, wotlkValue);

			vanillaValue = compactedListMap.get(vanillaValue);
			tbcValue = compactedListMap.get(tbcValue);
			wotlkValue = compactedListMap.get(wotlkValue);
		}
	}

	@Override
	protected TimeEntryMap<GameVersionId, V> newTimeEntryMap() {
		return new GameVersionEntryMap<>();
	}
}
