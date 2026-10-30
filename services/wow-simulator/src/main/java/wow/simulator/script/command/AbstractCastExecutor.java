package wow.simulator.script.command;

import wow.character.model.script.ScriptCommandCondition;
import wow.character.model.script.ScriptCommandTarget;
import wow.commons.model.spell.Ability;
import wow.simulator.model.unit.Unit;
import wow.simulator.script.ScriptParams;
import wow.simulator.script.prediction.PredictedState;

/**
 * User: POlszewski
 * Date: 2026-09-22
 */
public abstract class AbstractCastExecutor extends ComposableExecutor {
	protected AbstractCastExecutor(
			ScriptParams params,
			ScriptCommandCondition commandCondition,
			ScriptCommandTarget commandTarget,
			boolean optional
	) {
		super(params, commandCondition, commandTarget, optional);
	}

	protected abstract Ability getAbility();

	@Override
	public boolean allConditionsAreMet(PredictedState currentState) {
		var ability = getAbility();
		var target = getTarget();

		return currentState.allConditionsAreMet(commandCondition, ability, target);
	}

	@Override
	public void execute() {
		var target = getTarget();

		getActualCaster().cast(getAbility(), target);
	}

	protected Unit getTarget() {
		return switch (commandTarget) {
			case DEFAULT -> null;
			case SELF -> getActualCaster();
			case TARGET -> getActualCaster().getTarget();
			case FOCUS -> getActualCaster().getFocus();
			case MASTER -> getActualCaster().getMaster();
		};
	}
}
