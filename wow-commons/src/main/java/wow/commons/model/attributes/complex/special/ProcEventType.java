package wow.commons.model.attributes.complex.special;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2022-01-13
 */
public enum ProcEventType {
	SPELL_HIT,
	SPELL_CRIT,
	SPELL_RESIST,
	SPELL_DAMAGE;

	public static ProcEventType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
