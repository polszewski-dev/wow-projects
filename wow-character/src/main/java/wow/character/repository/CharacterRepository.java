package wow.character.repository;

import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CharacterTemplate;
import wow.character.model.character.CharacterTemplateId;
import wow.character.model.character.CombatRatingInfo;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.PhaseId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public interface CharacterRepository {
	Optional<GameVersion> getGameVersion(GameVersionId gameVersionId);

	Optional<Phase> getPhase(PhaseId phaseId);

	Optional<BaseStatInfo> getBaseStatInfo(GameVersionId gameVersionId, CharacterClassId characterClassId, RaceId raceId, int level);

	Optional<CombatRatingInfo> getCombatRatingInfo(GameVersionId gameVersionId, int level);

	Optional<CharacterTemplate> getCharacterTemplate(CharacterTemplateId characterTemplateId, CharacterClassId characterClassId, int level, PhaseId phaseId);

	Optional<CharacterTemplate> getDefaultCharacterTemplate(CharacterClassId characterClassId, int level, PhaseId phaseId);
}
