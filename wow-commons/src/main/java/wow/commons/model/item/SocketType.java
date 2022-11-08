package wow.commons.model.item;

/**
 * User: POlszewski
 * Date: 2021-03-06
 */
public enum SocketType {
	META,
	RED,
	YELLOW,
	BLUE;

	public boolean accepts(GemColor gemColor) {
		if (this == META) {
			return gemColor == GemColor.META;
		}
		else {
			return gemColor != GemColor.META;
		}
	}
}
