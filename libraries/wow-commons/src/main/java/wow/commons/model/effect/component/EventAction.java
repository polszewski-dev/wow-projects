package wow.commons.model.effect.component;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-09-15
 */
public enum EventAction {
	TRIGGER_SPELL,
	REMOVE,
	ADD_STACK,
	REMOVE_STACK,
	REMOVE_CHARGE,
	;

	public static EventAction parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
