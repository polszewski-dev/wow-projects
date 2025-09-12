package wow.commons.model.item;

/**
 * User: POlszewski
 * Date: 2025-09-12
 */
public record ItemId(int value) implements AbstractItemId, Comparable<ItemId> {
	public static ItemId of(int value) {
		return new ItemId(value);
	}

	@Override
	public int compareTo(ItemId other) {
		return Integer.compare(this.value, other.value);
	}
}
