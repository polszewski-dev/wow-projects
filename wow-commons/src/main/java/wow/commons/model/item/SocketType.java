package wow.commons.model.item;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
public enum SocketType {
	Meta,
	Red,
	Yellow,
	Blue
	;

	public static SocketType parse(char firstLetter) {
		for (SocketType socketType : values()) {
			if (socketType.name().charAt(0) == firstLetter) {
				return socketType;
			}
		}
		throw new IllegalArgumentException("Can't parse: " + firstLetter);
	}

	public boolean accepts(GemColor gemColor) {
		if (this == Meta) {
			return gemColor == GemColor.Meta;
		}
		else {
			return gemColor != GemColor.Meta;
		}
	}
}
