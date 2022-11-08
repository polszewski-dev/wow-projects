package wow.commons.model.categorization;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
public enum ArmorSubType implements ItemSubType {
	CLOTH("Cloth"),
	LEATHER("Leather"),
	MAIL("Mail"),
	PLATE("Plate");

	private final String key;

	ArmorSubType(String key) {
		this.key = key;
	}

	public static ArmorSubType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}
}
