package wow.character.repository;

import wow.character.model.build.BuildId;
import wow.character.model.build.BuildTemplate;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CombatRatingInfo;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public interface CharacterRepository {
	Optional<BaseStatInfo> getBaseStats(CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId);

	Optional<CombatRatingInfo> getCombatRatings(int level, PhaseId phaseId);

	Optional<BuildTemplate> getBuildTemplate(BuildId buildId, CharacterClassId characterClassId, int level, PhaseId phaseId);
}
