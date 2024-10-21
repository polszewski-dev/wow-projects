package wow.commons.model.profession;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-08-01
 */
public enum ProfessionType {
	CRAFTING,
	GATHERING,
	SECONDARY;

	public static ProfessionType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
