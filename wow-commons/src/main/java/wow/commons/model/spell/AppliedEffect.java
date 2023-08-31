package wow.commons.model.spell;

import wow.commons.model.Duration;
import wow.commons.model.effect.EffectId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
public record AppliedEffect(EffectId effectId, Duration duration) {
	public AppliedEffect {
		Objects.requireNonNull(effectId);
		Objects.requireNonNull(duration);
	}
}
