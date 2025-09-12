package wow.commons.model.item;

/**
 * User: POlszewski
 * Date: 2025-09-12
 */
public record TradedItemId(int value) implements AbstractItemId, Comparable<TradedItemId> {
	public static TradedItemId of(int value) {
		return new TradedItemId(value);
	}

	@Override
	public int compareTo(TradedItemId other) {
		return Integer.compare(this.value, other.value);
	}
}
