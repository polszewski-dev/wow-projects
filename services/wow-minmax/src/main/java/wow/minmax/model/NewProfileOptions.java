package wow.minmax.model;

import wow.commons.model.character.CharacterClass;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-09-03
 */
public record NewProfileOptions(
		List<CharacterClass> classOptions
) {
}
