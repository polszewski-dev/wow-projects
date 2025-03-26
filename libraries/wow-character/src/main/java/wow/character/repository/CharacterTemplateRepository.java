package wow.character.repository;

import wow.character.model.character.CharacterTemplate;
import wow.character.model.character.PlayerCharacter;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public interface CharacterTemplateRepository {
	Optional<CharacterTemplate> getCharacterTemplate(String name, PlayerCharacter character);

	Optional<CharacterTemplate> getDefaultCharacterTemplate(PlayerCharacter character);
}
