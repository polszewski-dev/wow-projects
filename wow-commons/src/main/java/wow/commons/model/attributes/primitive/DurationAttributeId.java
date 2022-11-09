package wow.commons.model.attributes.primitive;

import wow.commons.model.attributes.AttributeId;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-11-09
 */
public enum DurationAttributeId implements AttributeId {
	CAST_TIME_REDUCTION("CastTimeReduction", "reduced cast time"),
	COOLDOWN_REDUCTION("CooldownReduction");

	private final String key;
	private final String shortName;

	DurationAttributeId(String key, String shortName) {
		this.key = key;
		this.shortName = shortName;
	}

	DurationAttributeId(String key) {
		this(key, null);
	}

	public static DurationAttributeId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static DurationAttributeId tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public boolean isDurationAttribute() {
		return true;
	}

	@Override
	public int getSortOrder() {
		return 2000 + ordinal();
	}

	@Override
	public String toString() {
		return shortName != null ? shortName : key;
	}
}
