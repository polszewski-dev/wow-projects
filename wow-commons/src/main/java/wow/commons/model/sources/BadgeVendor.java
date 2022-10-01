package wow.commons.model.sources;

/**
 * User: POlszewski
 * Date: 2021-03-22
 */
class BadgeVendor extends NotSourcedFromInstance {
	BadgeVendor(Integer phase) {
		super(phase);
	}

	@Override
	protected int getDefaultPhase() {
		return -1;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof BadgeVendor;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return "BadgeVendor";
	}
}
