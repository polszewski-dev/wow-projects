package wow.commons.model.spells;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
public enum CostType {
	MANA,
	HEALTH,
	PET_MANA;

	public static CostType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
