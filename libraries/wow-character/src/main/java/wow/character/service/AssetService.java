package wow.character.service;

import wow.character.model.asset.AssetExecutionPlan;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.Raid;

/**
 * User: POlszewski
 * Date: 2026-03-11
 */
public interface AssetService {
	<P extends PlayerCharacter> AssetExecutionPlan<P> getAssetExecutionPlan(Raid<P> raid);
}
