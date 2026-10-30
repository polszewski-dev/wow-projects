package wow.simulator.script.command;

import wow.character.model.script.ScriptCommandTarget;
import wow.commons.model.spell.Ability;
import wow.simulator.model.unit.Unit;
import wow.simulator.script.ScriptParams;
import wow.simulator.script.prediction.PredictedState;

import static wow.character.model.script.ScriptCommand.TryMoveToTargetWithShortestDurationOf;

/**
 * User: POlszewski
 * Date: 2026-09-21
 */
public class TryMoveToTargetWithShortestDurationOfExecutor extends ComposableExecutor {
	private final Ability ability;

	private Unit newTarget;

	public TryMoveToTargetWithShortestDurationOfExecutor(TryMoveToTargetWithShortestDurationOf command, ScriptParams params) {
		super(params, command.condition(), ScriptCommandTarget.SELF, command.optional());
		this.ability = command.getAbility(params.caster());
	}

	public static TryMoveToTargetWithShortestDurationOfExecutor create(TryMoveToTargetWithShortestDurationOf command, ScriptParams params) {
		return new TryMoveToTargetWithShortestDurationOfExecutor(command, params);
	}

	@Override
	public boolean isValid() {
		return ability != null;
	}

	@Override
	public boolean allConditionsAreMet(PredictedState currentState) {
		newTarget = currentState.getEnemyWithShortestEffectDuration(ability);

		return newTarget != null && newTarget != caster.getTarget();
	}

	@Override
	public void execute() {
		caster.setTarget(newTarget);
		caster.emptyAction();
	}
}
