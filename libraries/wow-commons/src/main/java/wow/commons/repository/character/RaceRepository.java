package wow.commons.repository.character;

import wow.commons.model.character.Race;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.GameVersionId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
public interface RaceRepository {
	Optional<Race> getRace(RaceId raceId, GameVersionId gameVersionId);
}
