package wow.commons.model.spell;

import wow.commons.model.effect.EffectSource;

/**
 * User: POlszewski
 * Date: 2024-11-16
 */
public record EventCooldownId(EffectSource effectSource, int idx) implements CooldownId {
	public static EventCooldownId of(EffectSource effectSource, int idx) {
		return new EventCooldownId(effectSource, idx);
	}
}
