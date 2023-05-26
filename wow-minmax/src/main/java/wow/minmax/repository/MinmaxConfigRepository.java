package wow.minmax.repository;

import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.minmax.model.config.FindUpgradesConfig;
import wow.minmax.model.config.ViewConfig;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
public interface MinmaxConfigRepository {
	Optional<ViewConfig> getViewConfig(CharacterClassId characterClassId, PveRole role, GameVersionId gameVersionId);

	Optional<FindUpgradesConfig> getFindUpgradesConfig(CharacterClassId characterClassId, PveRole role, GameVersionId gameVersionId);
}
