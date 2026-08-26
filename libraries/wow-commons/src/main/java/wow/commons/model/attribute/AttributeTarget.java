package wow.commons.model.attribute;

import lombok.RequiredArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-10-14
 */
@RequiredArgsConstructor
public enum AttributeTarget {
	OWNER("Owner"),
	PET("Pet"),
	PARTY("Party");

	private final String key;

	public static AttributeTarget parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}
}
