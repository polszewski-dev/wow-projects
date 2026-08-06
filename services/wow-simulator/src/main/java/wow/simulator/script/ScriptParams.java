package wow.simulator.script;

import wow.simulator.model.unit.Player;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 06.08.2026
 */
public record ScriptParams(
		Player player,
		Player mainPlayer
) {
	public ScriptParams {
		Objects.requireNonNull(player);
	}
}
