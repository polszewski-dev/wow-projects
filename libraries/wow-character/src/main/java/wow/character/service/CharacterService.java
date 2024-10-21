package wow.character.service;

import wow.character.model.character.CharacterTemplateId;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.character.PlayerCharacter;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;

/**
 * User: POlszewski
 * Date: 2022-12-14
 */
public interface CharacterService {
	PlayerCharacter createPlayerCharacter(CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId);

	NonPlayerCharacter createNonPlayerCharacter(CreatureType creatureType, int level, PhaseId phaseId);

	void applyDefaultCharacterTemplate(PlayerCharacter character);

	void applyCharacterTemplate(PlayerCharacter character, CharacterTemplateId characterTemplateId);

	void updateAfterRestrictionChange(PlayerCharacter character);
}
