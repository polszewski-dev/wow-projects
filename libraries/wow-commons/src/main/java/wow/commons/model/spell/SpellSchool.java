package wow.commons.model.spell;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

import java.util.stream.Stream;

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
	NATURE("Nature"),
	PHYSICAL("Physical");

	private final String name;

	public static SpellSchool parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static SpellSchool tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	public static Stream<SpellSchool> magicSpellSchoolsStream() {
		return Stream.of(FROST, FIRE, ARCANE, SHADOW, HOLY, NATURE);
	}

	@Override
	public String toString() {
		return name;
	}
}
