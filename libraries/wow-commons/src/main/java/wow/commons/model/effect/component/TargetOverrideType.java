package wow.commons.model.effect.component;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2026-10-05
 */
public enum TargetOverrideType {
	PARENT_EVENT_TARGET;

	public static TargetOverrideType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
