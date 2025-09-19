package wow.character.model.script;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-09-17
 */
public record ScriptSection(
		List<ScriptCommand> commands
) {
	public static final ScriptSection EMPTY = new ScriptSection(List.of());

	public ScriptSection {
		Objects.requireNonNull(commands);
	}
}
