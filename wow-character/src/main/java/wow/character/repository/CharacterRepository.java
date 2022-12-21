package wow.character.repository;

import wow.character.model.build.BuildId;
import wow.character.model.build.BuildTemplate;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CombatRatingInfo;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
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
