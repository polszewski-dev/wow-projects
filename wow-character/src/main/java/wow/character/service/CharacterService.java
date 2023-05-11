package wow.character.service;

import wow.character.model.build.BuildId;
import wow.character.model.build.BuildTemplate;
import wow.character.model.character.Character;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;

/**
 * User: POlszewski
 * Date: 2022-12-14
 */
public interface CharacterService {
	BuildTemplate getBuildTemplate(BuildId buildId, Character character);

	Character createCharacter(CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId);

	void setDefaultBuild(Character character);

	void changeBuild(Character character, BuildId buildId);
}
