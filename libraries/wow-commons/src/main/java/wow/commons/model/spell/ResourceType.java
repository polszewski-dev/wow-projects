package wow.commons.model.spell;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
public enum ResourceType {
	MANA,
	ENERGY,
	RAGE,
	HEALTH,
	PET_MANA;

	public static ResourceType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
