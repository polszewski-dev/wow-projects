package wow.commons.model.sources;

import wow.commons.model.pve.Phase;

/**
 * User: POlszewski
 * Date: 2021-03-22
 */
class BadgeVendor extends NotSourcedFromInstance {
	BadgeVendor(Phase phase) {
		super(phase);
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
