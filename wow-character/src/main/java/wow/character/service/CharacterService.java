package wow.character.service;

import wow.character.model.build.BuildId;
import wow.character.model.build.BuildTemplate;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.Character;
import wow.character.model.character.CombatRatingInfo;

/**
 * User: POlszewski
 * Date: 2022-12-14
 */
public interface CharacterService {
	BaseStatInfo getBaseStats(Character character);

	CombatRatingInfo getCombatRatings(Character character);

	BuildTemplate getBuildTemplate(BuildId buildId, Character character);
}
