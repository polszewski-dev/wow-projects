package wow.commons.model.categorization;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-03
 */
public enum ItemCategory {
	WEAPON,
	ARMOR,
	ACCESSORY,
	QUEST,
	CONTAINER,
	PROJECTILE,
	TOKEN,
	ENCHANT,
	CRAFTING_MATERIAL,
	PATTERN;

	public static ItemCategory parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
