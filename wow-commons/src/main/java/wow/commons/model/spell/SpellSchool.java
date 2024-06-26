package wow.commons.model.spell;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2019-11-02
 */
@AllArgsConstructor
@Getter
public enum SpellSchool {
	FROST("Frost"),
	FIRE("Fire"),
	ARCANE("Arcane"),
	SHADOW("Shadow"),
	HOLY("Holy"),
	NATURE("Nature");

	private final String name;

	public static SpellSchool parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static SpellSchool tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
