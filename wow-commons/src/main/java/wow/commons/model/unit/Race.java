package wow.commons.model.unit;

/**
 * User: POlszewski
 * Date: 2021-03-19
 */
public enum Race {
	Undead,
	Orc,
	Troll,
	Tauren,
	BloodElf,
	;

	public static Race parse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return valueOf(value);
	}
}
