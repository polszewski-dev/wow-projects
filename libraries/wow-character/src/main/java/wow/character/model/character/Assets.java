package wow.character.model.character;

import wow.character.model.Copyable;
import wow.character.model.asset.Asset;
import wow.character.model.asset.AssetId;

/**
 * User: POlszewski
 * Date: 2026-03-06
 */
public class Assets extends Options<Asset, AssetId> implements Copyable<Assets> {
	@Override
	protected AssetId getId(Asset asset) {
		return asset.id();
	}

	@Override
	protected String getName(Asset asset) {
		return asset.name();
	}

	@Override
	protected String getKey(Asset asset) {
		if (asset.exclusionGroup() != null) {
			return asset.exclusionGroup().toString();
		}

		return asset.name();
	}

	@Override
	public Assets copy() {
		var copy = new Assets();
		copyInto(copy);
		return copy;
	}
}
