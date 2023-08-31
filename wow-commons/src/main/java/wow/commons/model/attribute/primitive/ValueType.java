package wow.commons.model.attribute.primitive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-03-16
 */
@AllArgsConstructor
@Getter
public enum ValueType {
	POINT("Point"),
	RATING("Rating"),
	PERCENT("Percent"),
	DURATION("Duration");

	private final String key;

	public static ValueType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static ValueType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}
}
