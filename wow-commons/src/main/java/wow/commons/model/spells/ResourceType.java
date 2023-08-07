package wow.commons.model.spells;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
public enum ResourceType {
	HEALTH,
	MANA,
	PET_MANA;

	public static ResourceType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
