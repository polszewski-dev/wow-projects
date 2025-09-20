package wow.simulator.script.command;

import wow.commons.model.spell.Ability;
import wow.simulator.model.unit.Player;

import static wow.character.model.script.ScriptCommand.CastSpellRank;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public class CastSpellRankExecutor extends ComposableExecutor {
	private CastSpellRankExecutor(CastSpellRank command, Player player) {
		super(player, command.conditions(), getAbility(command, player), command.target());
	}

	public static CastSpellRankExecutor create(CastSpellRank command, Player player) {
		return new CastSpellRankExecutor(command, player);
	}

	private static Ability getAbility(CastSpellRank command, Player player) {
		var name = command.abilityName();
		var rank = command.rank();

		return player.getAbility(name, rank).orElse(null);
	}
}
