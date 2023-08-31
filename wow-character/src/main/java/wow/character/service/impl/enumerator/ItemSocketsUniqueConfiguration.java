package wow.character.service.impl.enumerator;

import lombok.Getter;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.item.ItemSocketSpecification;
import wow.commons.model.item.SocketType;
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
	private final ItemSocketSpecification specification;

	ItemSocketsUniqueConfiguration(String key) {
		this.key = key;
		this.specification = new ItemSocketSpecification(
				key.chars()
						.mapToObj(letter -> parseSocketType((char)letter))
						.toList(),
				Attributes.EMPTY
		);
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

	private static SocketType parseSocketType(char firstLetter) {
		return switch (firstLetter) {
			case 'R' -> SocketType.RED;
			case 'Y' -> SocketType.YELLOW;
			case 'B' -> SocketType.BLUE;
			case 'M' -> SocketType.META;
			default -> throw new IllegalArgumentException("Can't parse: " + firstLetter);
		};
	}

	@Override
	public String toString() {
		return key;
	}
}
