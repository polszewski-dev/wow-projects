package wow.commons.model.attributes.primitive;

import wow.commons.model.attributes.AttributeId;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-11-09
 */
public enum BooleanAttributeId implements AttributeId {
	CAST_INSTANTLY("CastInstantly");

	private final String key;
	private final String shortName;

	BooleanAttributeId(String key, String shortName) {
		this.key = key;
		this.shortName = shortName;
	}

	BooleanAttributeId(String key) {
		this(key, null);
	}

	public static BooleanAttributeId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static BooleanAttributeId tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public boolean isBooleanAttribute() {
		return true;
	}

	@Override
	public int getSortOrder() {
		return 3000 + ordinal();
	}

	@Override
	public String toString() {
		return shortName != null ? shortName : key;
	}
}
