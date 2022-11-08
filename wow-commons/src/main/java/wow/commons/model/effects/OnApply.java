package wow.commons.model.effects;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-01-21
 */
public enum OnApply {
	STACK,
	REPLACE;

	public static OnApply parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
