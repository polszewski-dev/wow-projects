package wow.character.model.asset;

import wow.character.model.character.PlayerCharacter;

/**
 * User: POlszewski
 * Date: 2026-03-14
 */
public record AssetExecution<P extends PlayerCharacter>(
		P player,
		Asset asset
) {
	public String name() {
		return asset.name();
	}

	public int effectiveRank() {
		return asset.getEffectiveRank(player);
	}

	public Asset.Scope scope() {
		return asset.scope();
	}
}
