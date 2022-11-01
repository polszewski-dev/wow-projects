package wow.commons.model.sources;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
class PurchasedFromVendor extends NotSourcedFromInstance {
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
