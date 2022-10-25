package wow.commons.model.sources;

import wow.commons.model.pve.Phase;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
class PurchasedFromVendor extends NotSourcedFromInstance {
	PurchasedFromVendor(Phase phase) {
		super(phase);
	}

	@Override
	protected Phase getDefaultPhase() {
		return Phase.TBC_P0;
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
