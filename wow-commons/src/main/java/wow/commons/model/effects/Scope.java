package wow.commons.model.effects;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-01-17
 */
public enum Scope {
	PERSONAL,
	PARTY,
	RAID,
	GLOBAL;

	public static Scope parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
