package wow.commons.model.effect.component;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-09-15
 */
public enum ComponentType {
	DAMAGE,
	HEAL,
	MANA_DRAIN,
	MANA_GAIN,
	BASE_MANA_PCT_GAIN,
	ENERGY_GAIN,
	RAGE_GAIN,
	COPY_HEAL_PCT,
	COPY_DAMAGE_AS_HEAL_PCT,
	COPY_DAMAGE_AS_MANA_GAIN_PCT,
	COPY_HEALING_DONE_AS_HEAL_PCT,
	COPY_MANA_DRAINED_AS_DAMAGE_PCT,
	COPY_MANA_DRAINED_AS_MANA_PCT,
	COPY_MANA_GAINED_AS_MANA_GAIN_PCT,
	COPY_HEALTH_PAID_AS_MANA_GAIN_PCT,
	COPY_DAMAGE_ABSORBED_AS_DAMAGE_PCT,
	HEAL_DAMAGE_TAKEN_PCT,
	REFUND_COST_PCT,
	EXTRA_ATTACKS,
	THREAT_REDUCTION;

	public static ComponentType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
