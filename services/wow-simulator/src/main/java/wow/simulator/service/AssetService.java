package wow.simulator.service;

import wow.character.model.asset.Asset;
import wow.simulator.model.unit.Player;

/**
 * User: POlszewski
 * Date: 2025-12-13
 */
public interface AssetService {
	Player createPlayer(String name, Asset asset, Player mainPlayer);

	void executePreparationPhaseScripts(Player partyAsset, Asset asset, Player mainPlayer);

	void executeWarmUpPhaseScripts(Player partyAsset, Asset asset, Player mainPlayer);
}
