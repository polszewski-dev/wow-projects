package wow.commons.model.sources;

/**
 * User: POlszewski
 * Date: 2021-03-22
 */
public record WorldDrop() implements Source {
	@Override
	public boolean isWorldDrop() {
		return true;
	}

	@Override
	public String toString() {
		return "WorldDrop";
	}
}
