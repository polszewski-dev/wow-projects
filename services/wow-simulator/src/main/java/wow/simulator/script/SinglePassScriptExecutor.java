package wow.simulator.script;

import lombok.RequiredArgsConstructor;
import wow.character.model.script.ScriptCompiler;
import wow.character.model.script.ScriptSectionType;
import wow.simulator.script.command.ScriptCommandExecutor;

import java.util.List;

import static wow.character.model.script.ScriptPathResolver.getScriptPath;

/**
 * User: POlszewski
 * Date: 2025-12-13
 */
@RequiredArgsConstructor
public class SinglePassScriptExecutor {
	private final String scriptName;
	private final ScriptSectionType sectionType;
	private final ScriptParams params;
	private List<ScriptCommandExecutor> commands;

	public void setupPlayer() {
		var player = params.player();

		var scriptPath = getScriptPath(scriptName, player.getGameVersionId());
		var script = ScriptCompiler.compileResource(scriptPath);
		var section = script.getSection(sectionType);

		this.commands = section.commands().stream()
				.map(command -> ScriptCommandExecutor.create(command, params))
				.filter(ScriptCommandExecutor::isValid)
				.toList();
	}

	public void execute() {
		for (var command : commands) {
			command.execute();
		}
	}
}
