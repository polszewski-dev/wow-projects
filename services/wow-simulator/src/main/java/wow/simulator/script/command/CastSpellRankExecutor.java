package wow.simulator.script.command;

import wow.commons.model.spell.Ability;
import wow.simulator.model.unit.Player;
import wow.simulator.script.ScriptParams;

import static wow.character.model.script.ScriptCommand.CastSpellRank;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public class CastSpellRankExecutor extends ComposableExecutor {
	private CastSpellRankExecutor(CastSpellRank command, ScriptParams params) {
		super(params, command.condition(), getAbility(command, params.player()), command.target(), command.optional());
	}

	public static CastSpellRankExecutor create(CastSpellRank command, ScriptParams params) {
		return new CastSpellRankExecutor(command, params);
	}

	private static Ability getAbility(CastSpellRank command, Player player) {
		var name = command.abilityName();
		var rank = command.rank();

		return player.getAbility(name, rank).orElse(null);
	}
}
