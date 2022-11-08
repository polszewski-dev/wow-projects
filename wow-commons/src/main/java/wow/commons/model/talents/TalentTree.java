package wow.commons.model.talents;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2020-09-19
 */
public enum TalentTree {
	RACIAL,

	AFFLICTION,
	DEMONOLOGY,
	DESTRUCTION,

	MAGE_ARCANE,
	MAGE_FIRE,
	MAGE_FROST;

	public static TalentTree parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
