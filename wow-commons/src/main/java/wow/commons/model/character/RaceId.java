package wow.commons.model.character;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-19
 */
@AllArgsConstructor
public enum RaceId {
	UNDEAD("Undead"),
	ORC("Orc"),
	TROLL("Troll"),
	TAUREN("Tauren"),
	BLOOD_ELF("Blood Elf"),

	HUMAN("Human"),
	DWARF("Dwarf"),
	GNOME("Gnome"),
	NIGHT_ELF("Night Elf"),
	DRANEI("Dranei");

	private final String name;

	public static RaceId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
