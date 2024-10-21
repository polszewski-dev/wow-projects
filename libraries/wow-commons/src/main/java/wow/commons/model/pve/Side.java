package wow.commons.model.pve;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
public enum Side {
	HORDE,
	ALLIANCE;

	public static Side parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
