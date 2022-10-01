package wow.commons.model.sources;

/**
 * User: POlszewski
 * Date: 2021-04-03
 */
class PvP extends NotSourcedFromInstance {
	PvP(Integer phase) {
		super(phase);
	}

	@Override
	protected int getDefaultPhase() {
		return 0;
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
