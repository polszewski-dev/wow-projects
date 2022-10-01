package wow.commons.model.professions;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2020-07-14
 */
public enum Profession {
	Enchanting,
	Jewelcrafting,
	Inscription,
	Alchemy,
	Tailoring,
	Leatherworking,
	Blacksmithing,
	Engineering,
	Herbalism,
	Mining,
	Skinning,
	Cooking,
	Fishing,
	FirstAid
	;

	public static Profession tryParse(String value) {
		if (value == null) {
			return null;
		}
		return Stream.of(values()).filter(x -> x.name().equalsIgnoreCase(value)).findFirst().orElse(null);
	}
}
