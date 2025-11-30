package wow.commons.model.spell.component;

import java.util.List;
import java.util.Objects;

import static wow.commons.model.spell.component.ComponentCommand.DirectCommand;

/**
 * User: POlszewski
 * Date: 2023-09-16
 */
public record DirectComponent(
		List<DirectCommand> commands
) implements SpellComponent {
	public DirectComponent {
		Objects.requireNonNull(commands);
	}
}
