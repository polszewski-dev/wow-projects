package wow.commons.model.source;

/**
 * User: POlszewski
 * Date: 2023-06-29
 */
public record ContainedInItem(
		int id,
		String name
) implements Source {
	@Override
	public String toString() {
		return name;
	}
}
