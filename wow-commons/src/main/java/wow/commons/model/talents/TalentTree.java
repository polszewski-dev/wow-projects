package wow.commons.model.talents;

/**
 * User: POlszewski
 * Date: 2020-09-19
 */
public enum TalentTree {
	None,

	Trinket,
	Potion,

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
		for (TalentTree value : values()) {
			if (value.name().equalsIgnoreCase(name)) {
				return value;
			}
		}
		throw new IllegalArgumentException("Unknown talent: " + name);
	}
}
