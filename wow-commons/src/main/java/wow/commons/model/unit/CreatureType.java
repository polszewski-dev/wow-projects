package wow.commons.model.unit;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-19
 */
public enum CreatureType {
	HUMANOID,
	UNDEAD,
	DEMON,
	BEAST,
	DRAGON;

	public static CreatureType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
