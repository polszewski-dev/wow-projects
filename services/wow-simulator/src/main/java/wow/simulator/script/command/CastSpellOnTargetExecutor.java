package wow.simulator.script.command;

import wow.simulator.model.unit.Unit;
import wow.simulator.script.ScriptParams;

import static wow.character.model.script.ScriptCommand.CastSpell;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public class CastSpellOnTargetExecutor extends StaticExecutor {
	private final Unit actualTarget;

	private CastSpellOnTargetExecutor(CastSpell command, Unit actualTarget, ScriptParams params) {
		super(params, command.condition(), command.getAbility(params.caster()), command.target(), command.optional());
		this.actualTarget = actualTarget;
	}

	public static CastSpellOnTargetExecutor create(CastSpell command, Unit actualTarget, ScriptParams params) {
		return new CastSpellOnTargetExecutor(command, actualTarget, params);
	}

	@Override
	protected Unit getTarget() {
		return this.actualTarget;
	}
}
