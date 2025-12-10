package wow.character.repository.impl;

import org.springframework.stereotype.Component;
import wow.character.model.asset.AssetTemplate;
import wow.character.repository.AssetTemplateRepository;
import wow.character.repository.impl.parser.character.AssetTemplateExcelParser;
import wow.commons.model.pve.GameVersionId;
import wow.commons.util.GameVersionMap;

import java.io.IOException;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2025-12-12
 */
@Component
public class AssetTemplateRepositoryImpl implements AssetTemplateRepository {
	private final GameVersionMap<String, AssetTemplate> assetTemplateByName = new GameVersionMap<>();

	public AssetTemplateRepositoryImpl(AssetTemplateExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getAssetTemplates().forEach(this::addAssetTemplate);
	}

	@Override
	public Optional<AssetTemplate> getAssetTemplate(String name, GameVersionId gameVersionId) {
		return assetTemplateByName.getOptional(gameVersionId, name);
	}

	private void addAssetTemplate(AssetTemplate assetTemplate) {
		assetTemplateByName.put(assetTemplate.gameVersionId(), assetTemplate.name(), assetTemplate);
	}
}
