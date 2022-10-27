package wow.commons.model.unit;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2020-09-30
 */
public enum PetType {
	Imp,
	Voidwalker,
	Succubus,
	Felhunter,
	Felguard,
	;

	public static PetType parse(String name) {
		if (name == null) {
			return null;
		}
		return Stream.of(values())
				.filter(x -> x.name().equalsIgnoreCase(name))
				.findAny().orElseThrow(() -> new IllegalArgumentException(name));
	}
}
