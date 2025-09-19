package wow.simulator.script.command;

import wow.simulator.model.unit.Player;

import java.util.List;

import static wow.character.model.script.ScriptCommand.CastSequence;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public class CastSequenceExecutor extends ScriptCommandExecutor {
	private final List<ComposableExecutor> list;

	private CastSequenceExecutor(List<ComposableExecutor> list, Player player) {
		super(player);
		this.list = list;
	}

	public static CastSequenceExecutor create(CastSequence castSequence, Player player) {
		var list = castSequence.list().stream()
				.map(command -> ComposableExecutor.create(command, player))
				.toList();

		return new CastSequenceExecutor(list, player);
	}

	@Override
	public boolean isValid() {
		return list.stream().allMatch(ComposableExecutor::isValid);
	}

	@Override
	public boolean allConditionsAreMet() {
		return list.stream().allMatch(ComposableExecutor::allConditionsAreMet);
	}

	@Override
	public void execute() {
		list.forEach(ScriptCommandExecutor::execute);
	}
}
