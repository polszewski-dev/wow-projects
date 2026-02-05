package wow.character.repository;

import wow.character.model.asset.Asset;
import wow.commons.model.pve.PhaseId;

import java.util.Collection;

/**
 * User: POlszewski
 * Date: 2025-12-12
 */
public interface AssetRepository {
	Collection<Asset> getAvailableAssets(PhaseId phaseId);
}
