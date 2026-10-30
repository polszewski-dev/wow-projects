package wow.simulator.script.command;

import wow.simulator.script.ScriptParams;
import wow.simulator.script.prediction.PredictedState;

import java.util.ArrayList;
import java.util.List;

import static wow.character.model.script.ScriptCommand.CastSequence;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public class CastSequenceExecutor extends ScriptCommandExecutor {
	private final List<ComposableExecutor> list;
	private List<ComposableExecutor> toExecute;

	private CastSequenceExecutor(List<ComposableExecutor> list, ScriptParams params) {
		super(params);
		this.list = list;
	}

	public static CastSequenceExecutor create(CastSequence castSequence, ScriptParams params) {
		var list = castSequence.list().stream()
				.map(command -> ComposableExecutor.create(command, params))
				.filter(executor -> executor.isValid() || executor.isRequired())
				.toList();

		return new CastSequenceExecutor(list, params);
	}

	@Override
	public boolean isValid() {
		return !list.isEmpty() &&
				list.getLast().isRequired() &&
				list.stream().allMatch(ComposableExecutor::isValid);
	}

	@Override
	public boolean allConditionsAreMet(PredictedState currentState) {
		this.toExecute = getExecutors(currentState);

		return !toExecute.isEmpty();
	}

	private List<ComposableExecutor> getExecutors(PredictedState currentState) {
		var result = new ArrayList<ComposableExecutor>();

		for (var executor : list) {
			if (executor.allConditionsAreMet(currentState)) {
				result.add(executor);
				currentState = currentState.afterExecuting(executor);
			} else if (executor.isRequired()) {
				return List.of();
			}
		}

		return result;
	}

	@Override
	public void execute() {
		toExecute.forEach(ScriptCommandExecutor::execute);
	}
}
