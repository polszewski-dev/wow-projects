package wow.commons.model.effects;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-01-21
 */
public enum RemoveEvent {
	APPLY,
	BEGIN_CAST,
	END_CAST,
	DIRECT_DAMAGE_TAKEN;

	public static RemoveEvent parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
