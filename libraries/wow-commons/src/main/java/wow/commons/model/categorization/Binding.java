package wow.commons.model.categorization;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-03
 */
public enum Binding {
	NO_BINDING,
	BINDS_ON_EQUIP,
	BINDS_ON_PICK_UP;

	public static Binding parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
