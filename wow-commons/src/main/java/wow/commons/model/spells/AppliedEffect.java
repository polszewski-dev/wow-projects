package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.Duration;
import wow.commons.model.effects.EffectId;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
@AllArgsConstructor
@Getter
public class AppliedEffect {
	@NonNull
	private final EffectId effectId;
	@NonNull
	private final Duration duration;
}
