package wow.commons.model.sources;

import wow.commons.model.professions.Profession;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
class Crafted extends NotSourcedFromInstance {
	private final Profession profession;

	Crafted(Profession profession) {
		this.profession = profession;
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
