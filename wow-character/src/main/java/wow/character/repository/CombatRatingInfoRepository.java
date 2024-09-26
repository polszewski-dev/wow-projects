package wow.character.repository;

import wow.character.model.character.CombatRatingInfo;
import wow.commons.model.pve.GameVersionId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public interface CombatRatingInfoRepository {
	Optional<CombatRatingInfo> getCombatRatingInfo(GameVersionId gameVersionId, int level);
}
