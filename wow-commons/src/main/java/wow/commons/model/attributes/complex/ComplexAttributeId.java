package wow.commons.model.attributes.complex;

import wow.commons.model.attributes.AttributeId;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-11-09
 */
public enum ComplexAttributeId implements AttributeId {
	SPECIAL_ABILITIES("SpecialAbilities"),
	SOCKETS("Sockets"),
	SET_PIECES("SetPieces"),
	STAT_CONVERSION("StatConversion"),
	EFFECT_INCREASE_PER_EFFECT_ON_TARGET("EffectIncreasePerEffectOnTarget");

	private final String key;
	private final String shortName;

	ComplexAttributeId(String key, String shortName) {
		this.key = key;
		this.shortName = shortName;
	}

	ComplexAttributeId(String key) {
		this(key, null);
	}

	public static ComplexAttributeId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static ComplexAttributeId tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return shortName != null ? shortName : key;
	}
}
