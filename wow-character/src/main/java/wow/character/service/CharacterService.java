package wow.character.service;

import wow.character.model.character.Character;
import wow.character.model.character.CharacterTemplate;
import wow.character.model.character.CharacterTemplateId;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;

/**
 * User: POlszewski
 * Date: 2022-12-14
 */
public interface CharacterService {
	CharacterTemplate getCharacterTemplate(CharacterTemplateId characterTemplateId, Character character);

	Character createCharacter(CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId);

	void setDefaultBuild(Character character);

	void changeBuild(Character character, CharacterTemplateId characterTemplateId);
}
