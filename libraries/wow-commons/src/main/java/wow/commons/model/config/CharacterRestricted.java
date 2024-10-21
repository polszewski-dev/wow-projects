package wow.commons.model.config;

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
}
