package wow.commons.model.effect.component;

import wow.commons.model.Duration;

import java.util.List;
import java.util.Objects;

import static wow.commons.model.spell.component.ComponentCommand.PeriodicCommand;

/**
 * User: POlszewski
 * Date: 2023-09-16
 */
public record PeriodicComponent(
		List<PeriodicCommand> commands,
		Duration tickInterval
) implements EffectComponent {
	public PeriodicComponent {
		Objects.requireNonNull(commands);
		Objects.requireNonNull(tickInterval);
	}
}
