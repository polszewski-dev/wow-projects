package wow.commons.model.categorization;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-02
 */
@AllArgsConstructor
public enum ArmorSubType implements ItemSubType {
	CLOTH("Cloth"),
	LEATHER("Leather"),
	MAIL("Mail"),
	PLATE("Plate");

	private final String key;

	public static ArmorSubType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}
}
