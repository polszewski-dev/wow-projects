package wow.character.repository;

import wow.character.model.character.CharacterTemplate;
import wow.character.model.character.CharacterTemplateId;
import wow.character.model.character.GameVersion;
import wow.character.model.character.Phase;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public interface CharacterRepository {
	Optional<GameVersion> getGameVersion(GameVersionId gameVersionId);

	Optional<Phase> getPhase(PhaseId phaseId);

	Optional<CharacterTemplate> getCharacterTemplate(CharacterTemplateId characterTemplateId, CharacterClassId characterClassId, int level, PhaseId phaseId);
}
