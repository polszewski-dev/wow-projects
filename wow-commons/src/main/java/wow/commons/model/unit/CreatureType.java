package wow.commons.model.unit;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-19
 */
public enum CreatureType {
	Humanoid,
	Undead,
	Demon,
	Beast,
	Dragon,
	;

	public static CreatureType parse(String line) {
		return Stream.of(values()).filter(x -> x.name().equalsIgnoreCase(line)).findAny().orElseThrow(() -> new IllegalArgumentException(line));
	}
}
