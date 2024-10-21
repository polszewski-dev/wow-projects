package wow.commons.model.source;

/**
 * User: POlszewski
 * Date: 2021-03-22
 */
public record BadgeVendor() implements Source {
	@Override
	public boolean isBadgeVendor() {
		return true;
	}

	@Override
	public String toString() {
		return "BadgeVendor";
	}
}
