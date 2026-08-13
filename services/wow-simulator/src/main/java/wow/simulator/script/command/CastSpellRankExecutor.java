package wow.simulator.script.command;

import wow.simulator.script.ScriptParams;

import static wow.character.model.script.ScriptCommand.CastSpellRank;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public class CastSpellRankExecutor extends StaticExecutor {
	private CastSpellRankExecutor(CastSpellRank command, ScriptParams params) {
		super(params, command.condition(), command.getAbility(params.caster()), command.target(), command.optional());
	}

	public static CastSpellRankExecutor create(CastSpellRank command, ScriptParams params) {
		return new CastSpellRankExecutor(command, params);
	}
}
