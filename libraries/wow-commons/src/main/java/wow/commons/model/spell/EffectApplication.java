package wow.commons.model.spell;

import wow.commons.model.Duration;
import wow.commons.model.effect.Effect;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-28
 */
public record EffectApplication(
		SpellTarget target,
		Effect effect,
		Duration duration,
		int numStacks,
		int numCharges,
		EffectReplacementMode replacementMode
) {
	public EffectApplication {
		Objects.requireNonNull(target);
		Objects.requireNonNull(effect);
		Objects.requireNonNull(duration);
		Objects.requireNonNull(replacementMode);
	}

	public EffectApplication setEffect(Effect effect) {
		return new EffectApplication(target, effect, duration, numStacks, numCharges, replacementMode);
	}
}
