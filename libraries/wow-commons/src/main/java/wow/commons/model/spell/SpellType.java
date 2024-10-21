package wow.commons.model.spell;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-10-25
 */
public enum SpellType {
	CLASS_ABILITY,
	RACIAL_ABILITY,
	ACTIVATED_ABILITY,
	TRIGGERED_SPELL;

	public static SpellType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
