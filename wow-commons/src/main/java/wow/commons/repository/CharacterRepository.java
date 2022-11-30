package wow.commons.repository;

import wow.commons.model.character.BaseStatInfo;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CombatRatingInfo;
import wow.commons.model.character.Race;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public interface CharacterRepository {
	Optional<BaseStatInfo> getBaseStats(CharacterClass characterClass, Race race, int level);
	Optional<CombatRatingInfo> getCombatRatings(int level);
}
