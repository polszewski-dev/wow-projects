package wow.simulator.script.command;

import lombok.Getter;
import wow.character.model.script.ScriptCommandCondition;
import wow.character.model.script.ScriptCommandTarget;
import wow.commons.model.spell.Ability;
import wow.simulator.script.ScriptParams;

/**
 * User: POlszewski
 * Date: 12.08.2026
 */
@Getter
public abstract class StaticExecutor extends ComposableExecutor {
	protected final Ability ability;

	protected StaticExecutor(
			ScriptParams params,
			ScriptCommandCondition commandCondition,
			Ability ability,
			ScriptCommandTarget commandTarget,
			boolean optional
	) {
		super(params, commandCondition, commandTarget, optional);
		this.ability = ability;
	}

	@Override
	public boolean isValid() {
		return ability != null;
	}
}
