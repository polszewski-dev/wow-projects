package wow.commons.model.pve;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
public enum Side {
	HORDE,
	ALLIANCE,
	NEUTRAL,
	HOSTILE;

	public static Side parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public boolean isFriendlyWith(Side other) {
		return this == other || switch (this) {
			case HORDE, ALLIANCE -> other == NEUTRAL;
			case NEUTRAL -> other == HORDE || other == ALLIANCE;
			case HOSTILE -> false;
		};
	}
}
