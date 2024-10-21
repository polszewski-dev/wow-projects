package wow.commons.model.character;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2019-11-02
 */
@AllArgsConstructor
public enum CharacterClassId {
	MAGE("Mage"),
	WARLOCK("Warlock"),
	PRIEST("Priest"),
	DRUID("Druid"),
	ROGUE("Rogue"),
	HUNTER("Hunter"),
	SHAMAN("Shaman"),
	PALADIN("Paladin"),
	WARRIOR("Warrior");

	private final String name;

	public static CharacterClassId parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
