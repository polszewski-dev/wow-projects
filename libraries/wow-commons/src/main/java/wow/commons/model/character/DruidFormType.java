package wow.commons.model.character;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-09-29
 */
@AllArgsConstructor
public enum DruidFormType {
	BEAR_FORM("BearForm"),
	CAT_FORM("CatForm"),
	MOONKIN_FORM("MoonkinForm"),
	TREE_FORM("TreeForm"),
	CASTER_FORM("CasterForm");

	private final String name;

	public static DruidFormType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static DruidFormType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
