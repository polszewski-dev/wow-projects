package wow.simulator.util;

import lombok.Getter;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
@Getter
public abstract class SimpleId {
	private final long value;

	protected SimpleId(long value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o.getClass() == this.getClass() && value == ((SimpleId) o).value;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(value);
	}

	@Override
	public String toString() {
		return Long.toString(value);
	}
}
