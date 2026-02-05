package wow.character.repository.impl;

import org.springframework.stereotype.Component;
import wow.character.model.asset.Asset;
import wow.character.repository.AssetRepository;
import wow.character.repository.impl.parser.character.AssetExcelParser;
import wow.commons.model.pve.PhaseId;
import wow.commons.util.PhaseMap;

import java.io.IOException;
import java.util.Collection;

/**
 * User: POlszewski
 * Date: 2025-12-12
 */
@Component
public class AssetRepositoryImpl implements AssetRepository {
	private final PhaseMap<String, Asset> assetByName = new PhaseMap<>();

	public AssetRepositoryImpl(AssetExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getAssets().forEach(this::addAsset);
	}

	@Override
	public Collection<Asset> getAvailableAssets(PhaseId phaseId) {
		return assetByName.values(phaseId);
	}

	private void addAsset(Asset asset) {
		assetByName.putForEveryPhase(asset.timeRestriction().earliestPhaseId(), asset.name(), asset);
	}
}
