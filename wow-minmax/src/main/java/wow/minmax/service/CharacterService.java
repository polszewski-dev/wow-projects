package wow.minmax.service;

import wow.commons.model.character.*;

/**
 * User: POlszewski
 * Date: 2022-12-14
 */
public interface CharacterService {
	BaseStatInfo getBaseStats(CharacterInfo characterInfo);

	CombatRatingInfo getCombatRatings(CharacterInfo characterInfo);

	BuildTemplate getBuildTemplate(BuildId buildId, CharacterInfo characterInfo);
}
