package wow.commons.model.item;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2026-03-22
 */
public enum ConsumableExclusionGroup {
	POTION,
	STONE;

	public static ConsumableExclusionGroup parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
