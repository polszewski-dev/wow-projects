package wow.commons.model.talents;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2020-09-19
 */
public enum TalentTree {
	RACIAL("Racial"),

	AFFLICTION("Affliction"),
	DEMONOLOGY("Demonology"),
	DESTRUCTION("Destruction"),

	MAGE_ARCANE("MageArcane"),
	MAGE_FIRE("MageFire"),
	MAGE_FROST("MageFrost");

	private final String name;

	TalentTree(String name) {
		this.name = name;
	}

	public static TalentTree parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static TalentTree tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
