package wow.character.service;

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
	PlayerCharacter createPlayerCharacter(String name, CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId);

	<T extends PlayerCharacter> T createPlayerCharacter(String name, CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId, PlayerCharacterFactory<T> factory);

	NonPlayerCharacter createNonPlayerCharacter(String name, CreatureType creatureType, int level, PhaseId phaseId);

	<T extends NonPlayerCharacter> T createNonPlayerCharacter(String name, CreatureType creatureType, int level, PhaseId phaseId, NonPlayerCharacterFactory<T> factory);

	void applyDefaultCharacterTemplate(PlayerCharacter character);

	void applyCharacterTemplate(PlayerCharacter character, String templateName);

	void updateAfterRestrictionChange(PlayerCharacter character);

	void equipGearSet(PlayerCharacter character, String gearSetName);
}
