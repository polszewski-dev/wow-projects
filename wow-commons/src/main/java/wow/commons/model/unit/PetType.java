package wow.commons.model.unit;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2020-09-30
 */
public enum PetType {
	None,
	Imp,
	Voidwalker,
	Succubus,
	Felhunter,
	Felguard,
	;

	public static PetType parse(String line) {
		return Stream.of(values()).filter(x -> x.name().equalsIgnoreCase(line)).findAny().orElseThrow(() -> new IllegalArgumentException(line));
	}
}
