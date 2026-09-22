package wow.simulator.script.command;

import wow.character.model.script.ScriptCommandCondition;
import wow.character.model.script.ScriptCommandTarget;
import wow.simulator.script.ScriptParams;

/**
 * User: POlszewski
 * Date: 12.08.2026
 */
public abstract class DynamicExecutor extends AbstractCastExecutor {
	protected DynamicExecutor(
			ScriptParams params,
			ScriptCommandCondition commandCondition,
			ScriptCommandTarget commandTarget,
			boolean optional
	) {
		super(params, commandCondition, commandTarget, optional);
	}
}
