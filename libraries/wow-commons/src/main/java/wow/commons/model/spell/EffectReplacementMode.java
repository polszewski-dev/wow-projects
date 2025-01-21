package wow.commons.model.spell;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2025-01-20
 */
public enum EffectReplacementMode {
	DEFAULT,
	ADD_CHARGE;

	public static EffectReplacementMode parse(String value) {
		return EnumUtil.parse(value, values(), Enum::name);
	}
}
