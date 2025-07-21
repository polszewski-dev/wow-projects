package wow.simulator.script;

import wow.commons.model.spell.AbilityId;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;

import java.util.function.BiPredicate;

/**
 * User: POlszewski
 * Date: 2023-08-08
 */
public record ConditionalSpellCast(
		AbilityId abilityId,
		BiPredicate<Player, Unit> condition
) {
	public boolean check(Player player) {
		return condition.test(player, player.getTarget());
	}

	public static ConditionalSpellCast of(AbilityId spellToCast) {
		return new ConditionalSpellCast(spellToCast, (player, target) -> true);
	}

	public ConditionalSpellCast dotCondition() {
		return new ConditionalSpellCast(abilityId, condition.and(this::dotCondition));
	}

	public ConditionalSpellCast notOnCooldown() {
		return new ConditionalSpellCast(abilityId, condition.and(this::notOnCooldown));
	}

	private boolean dotCondition(Player player, Unit target) {
		if (player.isOnCooldown(abilityId)) {
			return false;
		}
		if (target.isUnderEffect(abilityId, player)) {
			return false;
		}
		if (!remainingDurationBelowSpellCastTime(player, target)) {
			return false;
		}
		return remainingSimulationDurationBelowSpellDuration(player, target);
	}

	private boolean notOnCooldown(Player player, Unit target) {
		return !player.isOnCooldown(abilityId);
	}

	private boolean remainingDurationBelowSpellCastTime(Player player, Unit target) {
		var optionalEffect = target.getEffect(abilityId, player);
		if (optionalEffect.isEmpty()) {
			return true;
		}
		var effect = optionalEffect.get();
		var remainingDuration = effect.getRemainingDurationSeconds();
		var castTime = player.getSpellCastSnapshot(abilityId).getCastTime();
		return remainingDuration <= castTime;
	}

	private boolean remainingSimulationDurationBelowSpellDuration(Player player, Unit target) {
		var castTime = player.getSpellCastSnapshot(abilityId).getCastTime();
		var duration = player.getEffectDurationSnapshot(abilityId, target).getDurationSeconds();
		var remainingTime = player.getSimulation().getRemainingTime().getSeconds();
		return castTime + duration <= remainingTime;
	}
}
