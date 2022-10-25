package wow.commons.model.sources;

import wow.commons.model.pve.Phase;

/**
 * User: POlszewski
 * Date: 2021-03-22
 */
class WorldDrop extends NotSourcedFromInstance {
	WorldDrop(Phase phase) {
		super(phase);
	}

	@Override
	protected Phase getDefaultPhase() {
		return Phase.TBC_P0;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof WorldDrop;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return "WorldDrop";
	}
}
