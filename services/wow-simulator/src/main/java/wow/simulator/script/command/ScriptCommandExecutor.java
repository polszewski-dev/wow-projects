package wow.simulator.script.command;

import wow.character.model.script.ScriptCommand;
import wow.character.model.script.ScriptCommandTarget;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;

import static wow.character.model.script.ScriptCommand.CastSequence;
import static wow.character.model.script.ScriptCommand.ComposableCommand;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public abstract class ScriptCommandExecutor {
	protected final Player player;
	protected final Player mainPlayer;

	protected ScriptCommandExecutor(Player player, Player mainPlayer) {
		this.player = player;
		this.mainPlayer = mainPlayer;
	}

	public static ScriptCommandExecutor create(ScriptCommand command, Player player, Player mainPlayer) {
		return switch (command) {
			case CastSequence castSequence -> CastSequenceExecutor.create(castSequence, player, mainPlayer);
			case ComposableCommand composableCommand -> ComposableExecutor.create(composableCommand, player, mainPlayer);
		};
	}

	public abstract boolean isValid();

	public abstract boolean allConditionsAreMet();

	public abstract void execute();

	protected Unit getTarget(ScriptCommandTarget target) {
		return switch (target) {
			case DEFAULT -> null;
			case SELF -> player;
			case TARGET -> player.getTarget();
			case MAIN -> mainPlayer;
		};
	}
}
