package wow.commons.model.item;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
public enum GemColor {
	Red(SocketType.Red),
	Orange(SocketType.Red, SocketType.Yellow),
	Yellow(SocketType.Yellow),
	Green(SocketType.Yellow, SocketType.Blue),
	Blue(SocketType.Blue),
	Purple(SocketType.Red, SocketType.Blue),
	Prismatic(SocketType.Red, SocketType.Yellow, SocketType.Blue),
	Meta(SocketType.Meta)
	;

	private final List<SocketType> matchingSocketTypes;

	GemColor(SocketType... matchingSocketTypes) {
		this.matchingSocketTypes = List.of(matchingSocketTypes);
	}

	public static GemColor parse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return valueOf(value);
	}

	public boolean matchesSocket(SocketType socket) {
		return matchingSocketTypes.contains(socket);
	}
}
