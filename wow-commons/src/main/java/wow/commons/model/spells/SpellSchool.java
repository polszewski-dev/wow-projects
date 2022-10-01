package wow.commons.model.spells;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2019-11-02
 */
public enum SpellSchool {
	None,
	Frost,
	Fire,
	Arcane,
	Shadow,
	Holy,
	Nature,

	;

	public static SpellSchool parse(String line) {
		return Stream.of(values()).filter(x -> x.name().equalsIgnoreCase(line)).findAny().orElseThrow(() -> new IllegalArgumentException(line));
	}
}
