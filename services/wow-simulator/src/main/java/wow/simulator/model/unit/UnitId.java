package wow.simulator.model.unit;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
public record UnitId(long value) implements Comparable<UnitId> {
	@Override
	public int compareTo(UnitId other) {
		return Long.compare(this.value, other.value);
	}
}
