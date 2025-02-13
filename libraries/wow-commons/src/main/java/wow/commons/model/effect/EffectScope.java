package wow.commons.model.effect;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2025-02-13
 */
public enum EffectScope {
	PERSONAL,
	GLOBAL;

	public static EffectScope parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
