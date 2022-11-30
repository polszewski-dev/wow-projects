package wow.commons.model.character;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-03-19
 */
@AllArgsConstructor
public enum CreatureType {
	HUMANOID("Humanoid"),
	UNDEAD("Undead"),
	DEMON("Demon"),
	BEAST("Beast"),
	DRAGON("Dragon");

	private final String name;

	public static CreatureType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static CreatureType tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
