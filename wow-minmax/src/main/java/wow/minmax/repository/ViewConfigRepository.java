package wow.minmax.repository;

import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.minmax.model.ViewConfig;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
public interface ViewConfigRepository {
	Optional<ViewConfig> getViewConfig(CharacterClassId characterClassId, PveRole role, GameVersionId gameVersionId);
}
