package wow.commons.model.item;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.util.EnumUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-01-03
 */
@Getter
public enum ItemSocketsUniqueConfiguration {
	NONE(""),

	R("R"),
	Y("Y"),
	B("B"),
	M("M"),

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
	BBB("BBB");

	private final String key;
	private ItemSocketSpecification specification;

	ItemSocketsUniqueConfiguration(String key) {
		this.key = key;
	}

	public static ItemSocketsUniqueConfiguration parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static ItemSocketsUniqueConfiguration of(List<SocketType> socketTypes) {
		String key = socketTypes
				.stream()
				.sorted()
				.map(socketType -> socketType.toString().substring(0, 1))
				.collect(Collectors.joining());
		return parse(key);
	}

	public ItemSocketSpecification getSocketSpecification() {
		if (specification == null) {
			this.specification = new ItemSocketSpecification(
					key.chars()
							.mapToObj(letter -> parseSocketType((char)letter))
							.collect(Collectors.toList()),
					Attributes.EMPTY
			);
		}
		return specification;
	}

	private static SocketType parseSocketType(char firstLetter) {
		switch (firstLetter) {
			case 'R':
				return SocketType.RED;
			case 'Y':
				return SocketType.YELLOW;
			case 'B':
				return SocketType.BLUE;
			case 'M':
				return SocketType.META;
			default:
				throw new IllegalArgumentException("Can't parse: " + firstLetter);
		}
	}

	@Override
	public String toString() {
		return key;
	}
}
