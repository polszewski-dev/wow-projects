package wow.simulator.script.command;

import wow.commons.model.spell.ActivatedAbility;
import wow.simulator.model.unit.Player;

import static wow.character.model.script.ScriptCommand.UseItem;

/**
 * User: POlszewski
 * Date: 2025-09-18
 */
public class UseItemExecutor extends ComposableExecutor {
	private UseItemExecutor(UseItem command, Player player, Player mainPlayer) {
		super(player, mainPlayer, command.condition(), getActivatedAbility(command, player), command.target(), command.optional());
	}

	public static UseItemExecutor create(UseItem command, Player player, Player mainPlayer) {
		return new UseItemExecutor(command, player, mainPlayer);
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
