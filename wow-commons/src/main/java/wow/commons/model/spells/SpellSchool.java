package wow.commons.model.spells;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2019-11-02
 */
public enum SpellSchool {
	FROST("Frost"),
	FIRE("Fire"),
	ARCANE("Arcane"),
	SHADOW("Shadow"),
	HOLY("Holy"),
	NATURE("Nature");

	private final String name;

	SpellSchool(String name) {
		this.name = name;
	}

	public static SpellSchool parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
