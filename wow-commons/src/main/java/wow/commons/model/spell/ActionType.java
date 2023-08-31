package wow.commons.model.spell;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-10-23
 */
public enum ActionType {
	PHYSICAL,
	SPELL;

	public static ActionType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
