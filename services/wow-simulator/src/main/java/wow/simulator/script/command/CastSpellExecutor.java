package wow.simulator.script.command;

import wow.commons.model.spell.Ability;
import wow.simulator.model.unit.Player;

import static wow.character.model.script.ScriptCommand.CastSpell;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public class CastSpellExecutor extends ComposableExecutor {
	private CastSpellExecutor(CastSpell command, Player player) {
		super(player, command.condition(), getAbility(command, player), command.target());
	}

	public static CastSpellExecutor create(CastSpell command, Player player) {
		return new CastSpellExecutor(command, player);
	}

	private static Ability getAbility(CastSpell command, Player player) {
		return player.getAbility(command.abilityId()).orElse(null);
	}
}
