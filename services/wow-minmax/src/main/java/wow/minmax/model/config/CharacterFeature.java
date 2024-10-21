package wow.minmax.model.config;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-08-06
 */
public enum CharacterFeature {
	COMBAT_RATINGS,
	WORLD_BUFFS,
	GEMS,
	GLYPHS,
	HEROICS;

	public static CharacterFeature parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
