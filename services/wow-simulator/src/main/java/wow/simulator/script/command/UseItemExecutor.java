package wow.simulator.script.command;

import wow.commons.model.spell.ActivatedAbility;
import wow.simulator.model.unit.Player;
import wow.simulator.script.ScriptParams;

import static wow.character.model.script.ScriptCommand.UseItem;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public class UseItemExecutor extends ComposableExecutor {
	private UseItemExecutor(UseItem command, ScriptParams params) {
		super(params, command.condition(), getActivatedAbility(command, params.player()), command.target(), command.optional());
	}

	public static UseItemExecutor create(UseItem command, ScriptParams params) {
		return new UseItemExecutor(command, params);
	}

	private static ActivatedAbility getActivatedAbility(UseItem useItem, Player player) {
		var itemSlot = useItem.itemSlot();
		var equippedItem = player.getEquippedItem(itemSlot);

		if (equippedItem == null) {
			return null;
		}

		return equippedItem.getItem().getActivatedAbility();
	}
}
