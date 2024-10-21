package wow.commons.util;

import wow.commons.model.pve.GameVersionId;

/**
 * User: POlszewski
 * Date: 2023-06-26
 */
public class GameVersionMap<K, V> extends TimeMap<K, V, GameVersionId> {
	public GameVersionMap() {
		super(GameVersionId.class);
	}
}
