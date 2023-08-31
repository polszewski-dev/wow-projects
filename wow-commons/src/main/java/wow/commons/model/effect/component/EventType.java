package wow.commons.model.effect.component;

import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-09-17
 */
public enum EventType {
	SPELL_CAST,
	SPELL_HIT,
	SPELL_CRIT,
	SPELL_DAMAGE,
	SPELL_HEAL,
	SPELL_RESISTED,
	PHYSICAL_HIT,
	PHYSICAL_CRIT,
	PHYSICAL_MISS,
	DODGE,
	PARRY,
	BLOCK,
	OWNER_ATTACKED,
	OWNER_RECEIVED_CRIT,
	ATTACK_BLOCKED,
	PET_DISMISS,
	ABSORPTION_GONE,
	EFFECT_TIMER,
	HEALED_TO_FULL,
	STACKS_MAXED,
	STACK_OVERFLOW,
	KILLED_TARGET,
	TARGET_DIED,
	OWNER_DIED
	;

	public static EventType parse(String value) {
		return EnumUtil.parse(value, values());
	}
}
