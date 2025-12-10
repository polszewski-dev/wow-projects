package wow.character.repository;

import wow.character.model.asset.AssetTemplate;
import wow.commons.model.pve.GameVersionId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2025-12-12
 */
public interface AssetTemplateRepository {
	Optional<AssetTemplate> getAssetTemplate(String name, GameVersionId gameVersionId);
}
