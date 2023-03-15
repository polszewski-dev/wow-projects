package wow.commons.model.attributes.primitive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-03-17
 */
@AllArgsConstructor
@Getter
public enum DamageType {
	ANY(""),
	DIRECT("Direct"),
	DOT("DoT");

	private final String key;

	public static DamageType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static DamageType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}
}
