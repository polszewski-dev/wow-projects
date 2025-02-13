package wow.commons.model.effect;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2025-02-13
 */
public enum EffectExclusionGroup {
	CURSE,
	ARMOR,
	FORTITUDE,
	SPIRIT,
	SHADOW_PROTECTION;

	public static EffectExclusionGroup parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
