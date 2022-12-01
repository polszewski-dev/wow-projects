package wow.commons.repository;

import wow.commons.model.character.*;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public interface CharacterRepository {
	Optional<BaseStatInfo> getBaseStats(CharacterClass characterClass, Race race, int level);
	Optional<CombatRatingInfo> getCombatRatings(int level);
	Optional<BuildTemplate> getBuildTemplate(BuildId buildId, CharacterClass characterClass, int level);
}
