package wow.commons.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.util.EnumUtil;

import static wow.commons.model.character.ExclusiveFaction.Group.ORACLES_FRENZYHEART_TRIBE;
import static wow.commons.model.character.ExclusiveFaction.Group.SCRYERS_ALDOR;

/**
 * User: POlszewski
 * Date: 2023-05-12
 */
@AllArgsConstructor
@Getter
public enum ExclusiveFaction {
	SCRYERS("The Scryers", SCRYERS_ALDOR),
	ALDOR("The Aldor", SCRYERS_ALDOR),
	ORACLES("The Oracles", ORACLES_FRENZYHEART_TRIBE),
	FRENZYHEART_TRIBE("Frenzyheart Tribe", ORACLES_FRENZYHEART_TRIBE);

	public enum Group {
		SCRYERS_ALDOR,
		ORACLES_FRENZYHEART_TRIBE
	}

	private final String name;
	private final Group group;

	public static ExclusiveFaction parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static ExclusiveFaction tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
