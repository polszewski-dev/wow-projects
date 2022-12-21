package wow.character.service;

import wow.character.model.build.BuildId;
import wow.character.model.build.BuildTemplate;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CharacterInfo;
import wow.character.model.character.CombatRatingInfo;

/**
 * User: POlszewski
 * Date: 2022-12-14
 */
public interface CharacterService {
	BaseStatInfo getBaseStats(CharacterInfo characterInfo);

	CombatRatingInfo getCombatRatings(CharacterInfo characterInfo);

	BuildTemplate getBuildTemplate(BuildId buildId, CharacterInfo characterInfo);
}
