package wow.simulator.script;

import lombok.Getter;
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
@Getter
public class SinglePassScriptExecutor {
	private final List<ScriptCommandExecutor> commands;

	public static SinglePassScriptExecutor compileScript(String scriptName, ScriptSectionType sectionType, ScriptParams params) {
		var caster = params.caster();

		var scriptPath = getScriptPath(scriptName, caster.getGameVersionId());
		var script = ScriptCompiler.compileResource(scriptPath);
		var section = script.getSection(sectionType);

		var commands = section.commands().stream()
				.map(command -> ScriptCommandExecutor.create(command, params))
				.filter(ScriptCommandExecutor::isValid)
				.toList();

		return new SinglePassScriptExecutor(commands);
	}

	public void execute() {
		for (var command : commands) {
			command.execute();
		}
	}
}
