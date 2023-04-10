package wow.commons.model.categorization;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-11-14
 */
public enum PveRole {
	CASTER_DPS;

	public static PveRole parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
