package wow.commons.model.effect.component;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-09-15
 */
public enum ComponentType {
	DAMAGE,
	HEAL,
	LOSE_MANA,
	GAIN_MANA,
	GAIN_PCT_OF_TOTAL_MANA,
	GAIN_PCT_OF_BASE_MANA,
	GAIN_ENERGY,
	GAIN_RAGE,
	COPY,
	HEAL_PCT_OF_DAMAGE_TAKEN,
	REFUND_COST_PCT,
	EXTRA_ATTACKS,
	ADD_STACK,
	REDUCE_THREAT,
	SUMMON,
	SACRIFICE;

	public static ComponentType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
