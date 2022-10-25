package wow.commons.model.sources;

import wow.commons.model.pve.Phase;

/**
 * User: POlszewski
 * Date: 2021-04-03
 */
class PvP extends NotSourcedFromInstance {
	PvP(Phase phase) {
		super(phase);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof PvP;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return "PvP";
	}
}
