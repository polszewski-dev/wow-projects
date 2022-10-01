package wow.commons.model.item;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2019-12-30
 */
public enum Tier {
	T1("T1"),
	T2("T2"),
	T25("T2.5"),
	T3("T3"),

	T4("T4"),
	T5("T5"),
	T6("T6"),
	;

	private final String name;

	Tier(String name) {
		this.name = name;
	}

	public static Tier parse(String line) {
		if (line == null || line.isEmpty()) {
			return null;
		}
		return Stream.of(values()).filter(x -> x.name.equals(line)).findAny().orElseThrow(IllegalArgumentException::new);
	}

	@Override
	public String toString() {
		return name;
	}
}
