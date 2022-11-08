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

	public boolean accepts(GemColor gemColor) {
		if (this == Meta) {
			return gemColor == GemColor.Meta;
		}
		else {
			return gemColor != GemColor.Meta;
		}
	}
}
