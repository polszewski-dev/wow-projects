package wow.commons.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-09-29
 */
@AllArgsConstructor
@Getter
public enum FormType {
	BEAR_FORM("BearForm"),
	CAT_FORM("CatForm"),
	MOONKIN_FORM("MoonkinForm"),
	TREE_FORM("TreeForm"),
	CASTER_FORM("CasterForm");

	private final String name;

	public static FormType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static FormType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
