package wow.character.service;

import wow.character.model.build.BuildId;
import wow.character.model.build.BuildTemplate;
import wow.character.model.build.Talents;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;

/**
 * User: POlszewski
 * Date: 2022-12-14
 */
public interface CharacterService {
	BaseStatInfo getBaseStats(Character character);

	CombatRatingInfo getCombatRatings(Character character);

	BuildTemplate getBuildTemplate(BuildId buildId, Character character);

	Talents getTalentsFromTalentLink(String link, Character character);

	Character createCharacter(CharacterClassId characterClassId, RaceId raceId, int level, BuildId buildId, CharacterProfessions professions, PhaseId phaseId);

	Enemy createEnemy(CreatureType enemyType);
}
