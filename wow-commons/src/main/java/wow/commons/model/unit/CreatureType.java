package wow.commons.model.unit;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-19
 */
public enum CreatureType {
	HUMANOID("Humanoid"),
	UNDEAD("Undead"),
	DEMON("Demon"),
	BEAST("Beast"),
	DRAGON("Dragon");

	private final String name;

	CreatureType(String name) {
		this.name = name;
	}

	public static CreatureType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
