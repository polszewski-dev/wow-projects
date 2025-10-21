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
	SHADOW_PROTECTION,
	ARCANE_INTELLECT,
	GIFT_OF_THE_WILD,
	BLESSING,
	SHIELD,
	FIRE_TOTEM,
	WATER_TOTEM,
	AIR_TOTEM,
	EARTH_TOTEM,
	FORM,
	BLESSING_OF_WISDOM,
	BLESSING_OF_KINGS
	;

	public static EffectExclusionGroup parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
