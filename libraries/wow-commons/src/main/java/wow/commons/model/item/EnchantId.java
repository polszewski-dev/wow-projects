package wow.commons.model.item;

/**
 * User: POlszewski
 * Date: 2025-09-12
 */
public record EnchantId(int value) implements Comparable<EnchantId> {
	public static EnchantId of(int value) {
		return new EnchantId(value);
	}

	@Override
	public int compareTo(EnchantId other) {
		return Integer.compare(this.value, other.value);
	}
}
