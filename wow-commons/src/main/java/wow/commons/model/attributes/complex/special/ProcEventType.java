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

	public double getProcChance(double hitChance, double critChance) {
		return switch (this) {
			case SPELL_HIT -> hitChance;
			case SPELL_CRIT -> critChance;
			case SPELL_RESIST -> 1 - hitChance;
			case SPELL_DAMAGE -> 1;
		};
	}
}
