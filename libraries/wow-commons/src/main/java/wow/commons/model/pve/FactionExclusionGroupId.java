package wow.commons.model.pve;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2025-09-28
 */
public enum FactionExclusionGroupId {
	SCRYERS_ALDOR,
	ORACLES_FRENZYHEART_TRIBE;

	public static FactionExclusionGroupId parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
