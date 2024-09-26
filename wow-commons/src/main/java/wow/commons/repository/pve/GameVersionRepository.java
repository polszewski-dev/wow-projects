package wow.commons.repository.pve;

import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.GameVersionId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public interface GameVersionRepository {
	Optional<GameVersion> getGameVersion(GameVersionId gameVersionId);
}
