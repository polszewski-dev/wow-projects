package wow.commons.model.item;

import wow.commons.util.EnumUtil;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
public enum GemColor {
	RED(SocketType.RED),
	ORANGE(SocketType.RED, SocketType.YELLOW),
	YELLOW(SocketType.YELLOW),
	GREEN(SocketType.YELLOW, SocketType.BLUE),
	BLUE(SocketType.BLUE),
	PURPLE(SocketType.RED, SocketType.BLUE),
	PRISMATIC(SocketType.RED, SocketType.YELLOW, SocketType.BLUE),
	META(SocketType.META);

	private final List<SocketType> matchingSocketTypes;

	GemColor(SocketType... matchingSocketTypes) {
		this.matchingSocketTypes = List.of(matchingSocketTypes);
	}

	public static GemColor parse(String value) {
		return EnumUtil.parse(value, values());
	}

	public boolean matchesSocket(SocketType socket) {
		return matchingSocketTypes.contains(socket);
	}
}
