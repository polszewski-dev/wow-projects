package wow.commons.model.spell;

import java.util.List;
import java.util.Objects;

import static wow.commons.model.spell.component.ComponentCommand.ApplyEffect;

/**
 * User: POlszewski
 * Date: 2023-09-28
 */
public record EffectApplication(
		List<ApplyEffect> commands
) {
	public EffectApplication {
		Objects.requireNonNull(commands);
	}
}
