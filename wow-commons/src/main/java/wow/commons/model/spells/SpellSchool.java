package wow.commons.model.spells;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2019-11-02
 */
public enum SpellSchool {
	Frost,
	Fire,
	Arcane,
	Shadow,
	Holy,
	Nature,

	;

	public static SpellSchool parse(String name) {
		if (name == null) {
			return null;
		}
		return Stream.of(values())
				.filter(x -> x.name().equalsIgnoreCase(name))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException(name));
	}
}
