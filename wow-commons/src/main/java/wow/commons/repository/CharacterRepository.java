package wow.commons.repository;

import wow.commons.model.character.*;
import wow.commons.model.pve.Phase;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public interface CharacterRepository {
	Optional<BaseStatInfo> getBaseStats(CharacterClass characterClass, Race race, int level, Phase phase);
	Optional<CombatRatingInfo> getCombatRatings(int level, Phase phase);
	Optional<BuildTemplate> getBuildTemplate(BuildId buildId, CharacterClass characterClass, int level, Phase phase);
}
