package wow.commons.model.config;

import wow.commons.model.character.CharacterInfo;

/**
 * User: POlszewski
 * Date: 2022-12-04
 */
public interface CharacterRestricted {
	CharacterRestriction getCharacterRestriction();

	default boolean isAvailableTo(CharacterInfo characterInfo) {
		return getCharacterRestriction().isMetBy(characterInfo);
	}
}
