package wow.commons.model.item;

/**
 * User: POlszewski
 * Date: 2025-09-12
 */
public record ConsumableId(int value) implements AbstractItemId, Comparable<ConsumableId> {
	public static ConsumableId of(int value) {
		return new ConsumableId(value);
	}

	@Override
	public int compareTo(ConsumableId other) {
		return Integer.compare(this.value, other.value);
	}
}
