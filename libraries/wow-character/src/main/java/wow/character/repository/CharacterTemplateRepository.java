package wow.character.repository;

import wow.character.model.character.CharacterTemplate;
import wow.character.model.character.CharacterTemplateId;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.PhaseId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public interface CharacterTemplateRepository {
	Optional<CharacterTemplate> getCharacterTemplate(CharacterTemplateId characterTemplateId, CharacterClassId characterClassId, int level, PhaseId phaseId);

	Optional<CharacterTemplate> getDefaultCharacterTemplate(CharacterClassId characterClassId, int level, PhaseId phaseId);
}
