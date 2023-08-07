package wow.simulator.scripts;

import wow.commons.model.spells.SpellId;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Target;

import java.util.function.BiPredicate;

/**
 * User: POlszewski
 * Date: 2023-08-08
 */
public record ConditionalSpellCast(
		SpellId spellToCast,
		BiPredicate<Player, Target> condition
) {
	public boolean check(Player player) {
		return condition.test(player, (Target) player.getTarget());
	}
}
