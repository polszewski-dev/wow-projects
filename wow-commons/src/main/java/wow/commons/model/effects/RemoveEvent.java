package wow.commons.model.effects;

/**
 * User: POlszewski
 * Date: 2021-01-21
 */
public enum RemoveEvent {
	APPLY,
	BEGIN_CAST,
	END_CAST,
	DIRECT_DAMAGE_TAKEN;

	public static RemoveEvent parse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return valueOf(value.toUpperCase());
	}
}
