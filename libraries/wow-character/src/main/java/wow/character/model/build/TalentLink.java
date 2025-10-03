package wow.character.model.build;

import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.talent.TalentNameRank;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-12-10
 */
public record TalentLink(
		String link,
		TalentLinkType type,
		GameVersionId gameVersionId,
		CharacterClassId characterClassId,
		List<TalentNameRank> talents
) {
	public TalentLink {
		Objects.requireNonNull(link);
		Objects.requireNonNull(type);
		Objects.requireNonNull(gameVersionId);
		Objects.requireNonNull(characterClassId);
		Objects.requireNonNull(talents);
	}

	public boolean matches(CharacterClassId characterClassId, GameVersionId gameVersionId) {
		return this.characterClassId == characterClassId && this.gameVersionId == gameVersionId;
	}
}
