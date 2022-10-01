package wow.commons.model.item;

import wow.commons.model.attributes.Attributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-01-03
 */
public enum ItemSocketsUniqueConfiguration {
	NONE(""),

	R("R"),
	Y("Y"),
	B("B"),

	RR("RR"),
	RY("RY"),
	RB("RB"),
	YY("YY"),
	YB("YB"),
	BB("BB"),

	MR("MR"),
	MY("MY"),
	MB("MB"),

	RRR("RRR"),
	RRY("RRY"),
	RRB("RRB"),
	RYY("RYY"),
	RYB("RYB"),
	RBB("RBB"),
	YYY("YYY"),
	YYB("YYB"),
	YBB("YBB"),
	BBB("BBB"),
	;

	private final String key;
	private ItemSocketSpecification specification;

	ItemSocketsUniqueConfiguration(String key) {
		this.key = key;
	}

	public static ItemSocketsUniqueConfiguration parse(String key) {
		for (ItemSocketsUniqueConfiguration value : values()) {
			if (value.key.equals(key)) {
				return value;
			}
		}
		throw new IllegalArgumentException(key);
	}

	public static ItemSocketsUniqueConfiguration of(List<SocketType> socketTypes) {
		String key = socketTypes
				.stream()
				.sorted()
				.map(socketType -> socketType.toString().substring(0, 1))
				.collect(Collectors.joining());
		return parse(key);
	}

	public String getKey() {
		return key;
	}

	public ItemSocketSpecification getSpecification() {
		if (specification == null) {
			this.specification = new ItemSocketSpecification(
					key.chars().mapToObj(letter -> SocketType.parse((char)letter)).collect(Collectors.toList()),
					Attributes.EMPTY
			);
		}
		return specification;
	}

	public int getSocketCount() {
		return getSpecification().getSocketCount();
	}

	public SocketType getSocketType(int socketNo) {
		return getSpecification().getSocketType(socketNo);
	}

	@Override
	public String toString() {
		return key;
	}
}
