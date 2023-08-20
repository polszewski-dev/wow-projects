package wow.simulator.scripts;

import wow.commons.model.spells.SpellId;
import wow.simulator.model.effect.Effect;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Target;

import java.util.function.BiPredicate;

/**
 * User: POlszewski
 * Date: 2023-08-08
 */
public record ConditionalSpellCast(
		SpellId spellId,
		BiPredicate<Player, Target> condition
) {
	public boolean check(Player player) {
		return condition.test(player, (Target) player.getTarget());
	}

	public static ConditionalSpellCast of(SpellId spellToCast) {
		return new ConditionalSpellCast(spellToCast, (player, target) -> true);
	}

	public ConditionalSpellCast dotCondition() {
		return new ConditionalSpellCast(spellId, condition.and(this::dotCondition));
	}

	public ConditionalSpellCast notOnCooldown() {
		return new ConditionalSpellCast(spellId, condition.and(this::notOnCooldown));
	}

	private boolean dotCondition(Player player, Target target) {
		if (player.isOnCooldown(spellId)) {
			return false;
		}
		if (target.isUnderEffect(spellId, player)) {
			return false;
		}
		if (!remainingDurationBelowSpellCastTime(player, target)) {
			return false;
		}
		return remainingSimulationDurationBelowSpellDuration(player);
	}

	private boolean notOnCooldown(Player player, Target target) {
		return !player.isOnCooldown(spellId);
	}

	private boolean remainingDurationBelowSpellCastTime(Player player, Target target) {
		var optionalEffect = target.getEffect(spellId, player);
		if (optionalEffect.isEmpty()) {
			return true;
		}
		Effect effect = optionalEffect.get();
		double remainingDuration = effect.getRemainingDuration().getSeconds();
		double castTime = player.getSnapshot(spellId).getCastTime();
		return remainingDuration <= castTime;
	}

	private boolean remainingSimulationDurationBelowSpellDuration(Player player) {
		double duration = player.getSnapshot(spellId).getDuration();
		double remainingTime = player.getSimulation().getRemainingTime().getSeconds();
		return duration <= remainingTime;
	}
}
