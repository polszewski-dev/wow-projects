package wow.commons.model.spell;

import wow.commons.model.effect.EffectSource;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
public sealed interface CooldownId permits AbilityCooldownId, EventCooldownId, GroupCooldownId {
	static AbilityCooldownId of(AbilityId abilityId) {
		return AbilityCooldownId.of(abilityId);
	}

	static EventCooldownId of(EffectSource effectSource, int idx) {
		return EventCooldownId.of(effectSource, idx);
	}
}
