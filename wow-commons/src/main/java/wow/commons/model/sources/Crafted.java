package wow.commons.model.sources;

import wow.commons.model.professions.Profession;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
class Crafted extends NotSourcedFromInstance {
	private final Profession profession;

	Crafted(Profession profession, Integer phase) {
		super(phase);
		this.profession = profession;
	}

	@Override
	protected int getDefaultPhase() {
		return -1;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Crafted;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return profession != null ? profession.toString(): "Crafted";
	}
}
