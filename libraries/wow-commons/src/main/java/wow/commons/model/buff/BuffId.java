package wow.commons.model.buff;

/**
 * User: POlszewski
 * Date: 2025-09-12
 */
public record BuffId(int value) implements Comparable<BuffId> {
	public static BuffId of(int value) {
		return new BuffId(value);
	}

	@Override
	public int compareTo(BuffId other) {
		return Integer.compare(this.value, other.value);
	}
}
