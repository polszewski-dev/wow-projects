package wow.simulator.script.command;

import wow.commons.model.spell.Ability;
import wow.simulator.model.unit.Player;
import wow.simulator.script.ScriptParams;

import static wow.character.model.script.ScriptCommand.CastSpell;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public class CastSpellExecutor extends ComposableExecutor {
	private CastSpellExecutor(CastSpell command, ScriptParams params) {
		super(params, command.condition(), getAbility(command, params.player()), command.target(), command.optional());
	}

	public static CastSpellExecutor create(CastSpell command, ScriptParams params) {
		return new CastSpellExecutor(command, params);
	}

	private static Ability getAbility(CastSpell command, Player player) {
		return player.getAbility(command.abilityId()).orElse(null);
	}
}
