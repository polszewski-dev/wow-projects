package wow.commons.model.spell;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
public enum CooldownGroup {
	POTION,
	CONJURED_ITEM,
	TRINKET;

	public static CooldownGroup parse(String value) {
		return EnumUtil.parse(value, values(), Enum::name);
	}
}
