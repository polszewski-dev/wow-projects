package wow.commons.model.talents;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2020-09-19
 */
public enum TalentTree {
	Racial,

	Affliction,
	Demonology,
	Destruction,

	MageArcane,
	MageFire,
	MageFrost,

	;

	public static TalentTree parse(String name) {
		if (name == null) {
			return null;
		}
		return Stream.of(values())
				.filter(x -> x.name().equalsIgnoreCase(name))
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException(name));
	}
}
