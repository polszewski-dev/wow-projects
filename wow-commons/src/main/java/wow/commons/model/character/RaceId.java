package wow.commons.model.character;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-19
 */
public enum RaceId {
	UNDEAD,
	ORC,
	TROLL,
	TAUREN,
	BLOOD_ELF,

	HUMAN,
	DWARF,
	GNOME,
	NIGHT_ELF,
	DRANEI;

	public static RaceId parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
