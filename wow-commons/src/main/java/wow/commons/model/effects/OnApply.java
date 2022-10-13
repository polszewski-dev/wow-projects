package wow.commons.model.effects;

/**
 * User: POlszewski
 * Date: 2021-01-21
 */
public enum OnApply {
	STACK,
	REPLACE;

	public static OnApply parse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return valueOf(value.toUpperCase());
	}
}
