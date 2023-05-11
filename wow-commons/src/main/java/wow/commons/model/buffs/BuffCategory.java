package wow.commons.model.buffs;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-05-10
 */
public enum BuffCategory {
	SELF_BUFF,
	PARTY_BUFF,
	RAID_BUFF,
	WORLD_BUFF,
	CONSUME;

	public static BuffCategory parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
