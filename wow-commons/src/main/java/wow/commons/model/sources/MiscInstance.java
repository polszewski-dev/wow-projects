package wow.commons.model.sources;

import wow.commons.model.pve.Instance;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
class MiscInstance extends SourcedFromInstance {
	MiscInstance(Instance instance, Integer phase) {
		super(instance, phase);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MiscInstance misc = (MiscInstance) o;
		return instance == misc.instance;
	}

	@Override
	public int hashCode() {
		return Objects.hash(instance);
	}

	@Override
	public String toString() {
		return "Misc instance: " + instance;
	}
}
