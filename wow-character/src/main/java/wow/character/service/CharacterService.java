package wow.character.service;

import wow.character.model.character.Character;
import wow.character.model.character.CharacterTemplateId;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;

/**
 * User: POlszewski
 * Date: 2022-12-14
 */
public interface CharacterService {
	Character createCharacter(CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId);

	void applyCharacterTemplate(Character character, CharacterTemplateId characterTemplateId);

	void onTalentsChange(Character character);
}
