package wow.simulator.script.command;

import lombok.Getter;
import wow.character.model.script.ScriptCommandCondition;
import wow.character.model.script.ScriptCommandTarget;
import wow.simulator.script.ScriptParams;

import static wow.character.model.script.ScriptCommand.*;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public abstract class ComposableExecutor extends ScriptCommandExecutor {
	protected final ScriptCommandCondition commandCondition;
	protected final ScriptCommandTarget commandTarget;
	@Getter
	protected final boolean optional;

	protected ComposableExecutor(
			ScriptParams params,
			ScriptCommandCondition commandCondition,
			ScriptCommandTarget commandTarget,
			boolean optional
	) {
		super(params);
		this.commandCondition = commandCondition;
		this.commandTarget = commandTarget;
		this.optional = optional;
	}

	public static ComposableExecutor create(ComposableCommand command, ScriptParams params) {
		return switch (command) {
			case CastSpell castSpell -> CastSpellExecutor.create(castSpell, params);
			case CastSpellRank castSpellRank -> CastSpellRankExecutor.create(castSpellRank, params);
			case CastPetSpell castPetSpell -> CastPetSpellExecutor.create(castPetSpell, params);
			case UseItem useItem -> UseItemExecutor.create(useItem, params);
			case TryMoveToTargetWithShortestDurationOf tryMoveToTargetWithShortestDurationOf -> TryMoveToTargetWithShortestDurationOfExecutor.create(tryMoveToTargetWithShortestDurationOf, params);
		};
	}
}
