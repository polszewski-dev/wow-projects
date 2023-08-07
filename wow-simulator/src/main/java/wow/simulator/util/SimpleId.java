package wow.simulator.util;

/**
 * User: POlszewski
 * Date: 2023-08-10
 */
public abstract class SimpleId {
	private final String id;

	protected SimpleId(String prefix, long value) {
		this.id = prefix + "%04d".formatted(value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SimpleId simpleId)) return false;

		return id.equals(simpleId.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return id;
	}
}
