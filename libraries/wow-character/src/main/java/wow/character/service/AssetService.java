package wow.character.service;

import wow.character.model.asset.AssetExecution;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.Raid;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2026-03-11
 */
public interface AssetService {
	<P extends PlayerCharacter> List<AssetExecution<P>> getAssetExecutionPlan(Raid<P> raid);
}
