package wow.simulator.script.command;

import wow.simulator.script.ScriptParams;

import static wow.character.model.script.ScriptCommand.UseItem;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public class UseItemExecutor extends ComposableExecutor {
	private UseItemExecutor(UseItem command, ScriptParams params) {
		super(params, command.condition(), command.getActivatedAbility(params.player()), command.target(), command.optional());
	}

	public static UseItemExecutor create(UseItem command, ScriptParams params) {
		return new UseItemExecutor(command, params);
	}
}
