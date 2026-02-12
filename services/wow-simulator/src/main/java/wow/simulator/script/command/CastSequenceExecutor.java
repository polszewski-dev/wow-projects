package wow.simulator.script.command;

import wow.simulator.model.unit.Player;

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

	private CastSequenceExecutor(List<ComposableExecutor> list, Player player, Player mainPlayer) {
		super(player, mainPlayer);
		this.list = list;
	}

	public static CastSequenceExecutor create(CastSequence castSequence, Player player, Player mainPlayer) {
		var list = castSequence.list().stream()
				.map(command -> ComposableExecutor.create(command, player, mainPlayer))
				.filter(executor -> executor.isValid() || !executor.isOptional())
				.toList();

		return new CastSequenceExecutor(list, player, mainPlayer);
	}

	@Override
	public boolean isValid() {
		return !list.isEmpty() &&
				!list.getLast().isOptional() &&
				list.stream().allMatch(ComposableExecutor::isValid);
	}

	@Override
	public boolean allConditionsAreMet() {
		this.toExecute = new ArrayList<>();

		for (var executor : list) {
			if (executor.allConditionsAreMet()) {
				toExecute.add(executor);
			} else if (!executor.isOptional()) {
				toExecute = null;
				return false;
			}
		}

		return true;
	}

	@Override
	public void execute() {
		if (toExecute == null) {
			list.forEach(ScriptCommandExecutor::execute);
		} else {
			toExecute.forEach(ScriptCommandExecutor::execute);
			toExecute = null;
		}
	}
}
