package wow.commons.repository.character;

import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 27.09.2024
 */
public interface CharacterClassRepository {
	Optional<CharacterClass> getCharacterClass(CharacterClassId characterClassId, GameVersionId gameVersionId);
}
