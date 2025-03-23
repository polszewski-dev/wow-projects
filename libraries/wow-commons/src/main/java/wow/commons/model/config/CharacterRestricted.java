package wow.commons.model.config;

import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-04
 */
public interface CharacterRestricted {
	CharacterRestriction getCharacterRestriction();

	default boolean isAvailableTo(CharacterInfo characterInfo) {
		return getCharacterRestriction().isMetBy(characterInfo);
	}

	default Integer getRequiredLevel() {
		return getCharacterRestriction().level();
	}

	default Integer getRequiredMaxLevel() {
		return getCharacterRestriction().maxLevel();
	}

	default List<CharacterClassId> getRequiredCharacterClassIds() {
		return getCharacterRestriction().characterClassIds();
	}

	default PveRole getRequiredRole() {
		return getCharacterRestriction().role();
	}
}
