package wow.commons.model.sources;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
class PurchasedFromVendor extends NotSourcedFromInstance {
	PurchasedFromVendor(Integer phase) {
		super(phase);
	}

	@Override
	protected int getDefaultPhase() {
		return -1;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof PurchasedFromVendor;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return "Vendor";
	}
}
