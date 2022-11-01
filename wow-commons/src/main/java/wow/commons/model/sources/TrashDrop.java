package wow.commons.model.sources;

import wow.commons.model.pve.Zone;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
class TrashDrop extends SourcedFromInstance {
	TrashDrop(Zone instance) {
		super(instance);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TrashDrop trashDrop = (TrashDrop) o;
		return instance == trashDrop.instance;
	}

	@Override
	public int hashCode() {
		return Objects.hash(instance);
	}

	@Override
	public String toString() {
		return "Trash: " + instance;
	}
}
