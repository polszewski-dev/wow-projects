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
	REMOVE_CHARGE_AND_TRIGGER_SPELL,
	;

	public static EventAction parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
