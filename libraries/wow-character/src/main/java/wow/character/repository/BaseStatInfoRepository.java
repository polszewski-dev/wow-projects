package wow.character.repository;

import wow.character.model.character.BaseStatInfo;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.GameVersionId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public interface BaseStatInfoRepository {
	Optional<BaseStatInfo> getBaseStatInfo(GameVersionId gameVersionId, CharacterClassId characterClassId, RaceId raceId, int level);
}
