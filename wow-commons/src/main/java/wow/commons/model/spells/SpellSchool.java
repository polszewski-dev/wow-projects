package wow.commons.model.spells;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2019-11-02
 */
public enum SpellSchool {
	FROST,
	FIRE,
	ARCANE,
	SHADOW,
	HOLY,
	NATURE;

	public static SpellSchool parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
