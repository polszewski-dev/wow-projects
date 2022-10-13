package wow.commons.model.attributes.complex.modifiers;

/**
 * User: POlszewski
 * Date: 2022-01-13
 */
public enum ProcEvent {
	SPELL_HIT,
	SPELL_CRIT,
	SPELL_RESIST,
	SPELL_DAMAGE;

	public static ProcEvent parse(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return valueOf(value.toUpperCase());
	}
}
