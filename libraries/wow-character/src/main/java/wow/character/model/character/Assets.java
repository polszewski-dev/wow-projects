package wow.character.model.character;

import wow.character.model.asset.Asset;
import wow.character.model.asset.AssetId;

/**
 * User: POlszewski
 * Date: 2026-03-06
 */
public class Assets extends Options<Asset, AssetId> {
	private final PlayerCharacter owner;

	public Assets(PlayerCharacter owner) {
		this.owner = owner;
	}

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
	protected void optionEnabled(Asset asset) {
		if (asset != null) {
			owner.assetEnabled(asset);
		}
	}

	@Override
	protected void optionDisabled(Asset asset) {
		if (asset != null) {
			owner.assetDisabled(asset);
		}
	}
}
