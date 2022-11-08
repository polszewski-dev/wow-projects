package wow.commons.model.buffs;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-12-26
 */
public enum BuffExclusionGroup {
	BUFF,
	SELF_BUFF,
	OIL,
	FLASK,
	FOOD,
	POTION,
	RACIAL,

	ARMOR,
	DEMONIC_SACRIFICE,
	COE;

	public static BuffExclusionGroup parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
