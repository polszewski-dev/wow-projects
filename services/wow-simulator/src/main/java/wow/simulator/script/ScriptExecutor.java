package wow.simulator.script;

import lombok.RequiredArgsConstructor;
import wow.character.model.script.ScriptCompiler;
import wow.character.model.script.ScriptPathResolver;
import wow.commons.model.Duration;
import wow.simulator.model.unit.Player;
import wow.simulator.script.command.ScriptCommandExecutor;

import java.util.List;

import static wow.character.model.script.ScriptSectionType.ROTATION;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
@RequiredArgsConstructor
public class ScriptExecutor {
	private final Player player;
	private final Player mainPlayer;

	private List<ScriptCommandExecutor> rotationCommands;

	public void setupPlayer() {
		var scriptPath = ScriptPathResolver.getScriptPath(player);
		var script = ScriptCompiler.compileResource(scriptPath);
		var rotationSection = script.getSection(ROTATION);

		this.rotationCommands = rotationSection.commands().stream()
				.map(command -> ScriptCommandExecutor.create(command, player, mainPlayer))
				.filter(ScriptCommandExecutor::isValid)
				.toList();

		player.setOnPendingActionQueueEmpty(x -> execute());
	}

	public void execute() {
		var command = getFirstAvailableCommand();

		if (command != null) {
			command.execute();
		} else {
			player.idleFor(Duration.seconds(1));
		}
	}

	private ScriptCommandExecutor getFirstAvailableCommand() {
		return rotationCommands.stream()
				.filter(ScriptCommandExecutor::allConditionsAreMet)
				.findFirst()
				.orElse(null);
	}
}
