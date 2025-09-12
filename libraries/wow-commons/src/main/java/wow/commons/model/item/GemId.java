package wow.commons.model.item;

/**
 * User: POlszewski
 * Date: 2025-09-12
 */
public record GemId(int value) implements AbstractItemId, Comparable<GemId> {
	public static GemId of(int value) {
		return new GemId(value);
	}

	@Override
	public int compareTo(GemId other) {
		return Integer.compare(this.value, other.value);
	}
}
