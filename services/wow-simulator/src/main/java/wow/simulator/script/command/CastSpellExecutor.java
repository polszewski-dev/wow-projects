package wow.simulator.script.command;

import wow.simulator.script.ScriptParams;

import static wow.character.model.script.ScriptCommand.CastSpell;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public class CastSpellExecutor extends StaticExecutor {
	private CastSpellExecutor(CastSpell command, ScriptParams params) {
		super(params, command.condition(), command.getAbility(params.caster()), command.target(), command.optional());
	}

	public static CastSpellExecutor create(CastSpell command, ScriptParams params) {
		return new CastSpellExecutor(command, params);
	}
}
