package wow.commons.model.sources;

/**
 * User: POlszewski
 * Date: 2021-03-22
 */
class WorldDrop extends NotSourcedFromInstance {
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
