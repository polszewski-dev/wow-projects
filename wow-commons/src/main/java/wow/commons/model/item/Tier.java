package wow.commons.model.item;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2019-12-30
 */
public enum Tier {
	T1("T1"),
	T2("T2"),
	T2_5("T2.5"),
	T3("T3"),

	T4("T4"),
	T5("T5"),
	T6("T6");

	private final String name;

	Tier(String name) {
		this.name = name;
	}

	public static Tier parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
