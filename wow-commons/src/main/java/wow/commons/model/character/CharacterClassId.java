package wow.commons.model.character;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2019-11-02
 */
public enum CharacterClassId {
	MAGE,
	WARLOCK,
	PRIEST,
	DRUID,
	ROGUE,
	HUNTER,
	SHAMAN,
	PALADIN,
	WARRIOR;

	public static CharacterClassId parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
