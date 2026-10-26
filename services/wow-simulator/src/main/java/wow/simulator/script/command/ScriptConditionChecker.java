package wow.simulator.script.command;

import wow.character.model.script.ScriptCommandCondition;
import wow.commons.model.AnyDuration;
import wow.commons.model.Duration;
import wow.commons.model.InfiniteDuration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;

import java.util.regex.Pattern;

import static wow.character.model.script.ScriptCommandCondition.*;

/**
 * User: POlszewski
 * Date: 2025-10-07
 */
public class ScriptConditionChecker {
	private final Unit caster;
	private final Ability ability;
	private final Unit primaryTarget;
	private final Unit target;

	public ScriptConditionChecker(Unit caster, Ability ability, Unit primaryTarget) {
		this.caster = caster;
		this.ability = ability;
		this.primaryTarget = primaryTarget;
		this.target = caster.getTarget();
	}

	public boolean check(ScriptCommandCondition condition) {
		return switch (condition) {
			case Or(var left, var right) ->
					check(left) || check(right);

			case And(var left, var right) ->
					check(left) && check(right);

			case Not(var right) ->
					!check(right);

			case LessThan(var left, var right) ->
					eval(left) < eval(right);

			case LessThanOrEqual(var left, var right) ->
					eval(left) <= eval(right);

			case GreaterThan(var left, var right) ->
					eval(left) > eval(right);

			case GreaterThanOrEqual(var left, var right) ->
					eval(left) >= eval(right);

			case EmptyCondition() -> true;

			case CasterHasEffect(var effectNamePattern) ->
					caster.hasEffect(getPattern(effectNamePattern));

			case TargetHasEffect(var effectNamePattern) ->
					target.hasEffect(getPattern(effectNamePattern), caster);

			case CanCastMoreBeforeSimulationEnds(var abilityId) ->
					canCastMoreBeforeSimulationEnds(abilityId);

			case AssetEnabled(var assetName) ->
					caster instanceof Player player && player.getAssets().has(assetName);
		};
	}

	private double eval(Expression expression) {
		return switch (expression) {
			case Constant(var value) ->
					value;

			case CasterHealth() ->
					caster.getCurrentHealth();

			case CasterMana() ->
					caster.getCurrentMana();

			case CasterHealthPct() ->
					caster.getHealthPct().value();

			case CasterManaPct() ->
					caster.getManaPct().value();

			case TargetHealthPct() ->
					target.getHealthPct().value();

			case TargetManaPct() ->
					target.getManaPct().value();

			case FocusHealthPct() ->
					caster.getFocus() != null ? caster.getFocus().getHealthPct().value() : 0;

			case FocusManaPct() ->
					caster.getFocus() != null ? caster.getFocus().getManaPct().value() : 0;

			case PetHealthPct() ->
					caster.getActivePet() != null ? caster.getActivePet().getHealthPct().value() : 0;

			case PetManaPct() ->
					caster.getActivePet() != null ? caster.getActivePet().getManaPct().value() : 0;

			case MasterHealthPct() ->
					caster.getMaster().getHealthPct().value();

			case MasterManaPct() ->
					caster.getMaster().getManaPct().value();

			case FullDuration() ->
					getFullDuration(ability.getAbilityId());

			case RemainingCooldown(var abilityId) ->
					getRemainingCooldown(abilityId);

			case RemainingSimulationDuration() ->
					getRemainingSimulationDuration();

			case TargetNumStacksOfAny(var effectName) ->
					target.getNumStacks(effectName);
			
			case TargetRemainingEffectDurationOfAny(var effectName) ->
					getRemainingAnyEffectDuration(effectName);
		};
	}

	private double getCastTime(AbilityId abilityId) {
		var snapshot = caster.getSpellCastSnapshot(abilityId, primaryTarget);

		return snapshot.getCastTime();
	}

	private double getFullDuration(AbilityId abilityId) {
		var snapshot = caster.getEffectDurationSnapshot(abilityId, primaryTarget);

		return getSeconds(snapshot.getDuration());
	}

	private double getRemainingCooldown(AbilityId abilityId) {
		return caster.getRemainingCooldown(abilityId).getSeconds();
	}

	private double getRemainingSimulationDuration() {
		return caster.getSimulation().getRemainingTime().getSeconds();
	}

	private double getRemainingAnyEffectDuration(String effectName) {
		var effectDuration = target.getRemainingEffectDuration(effectName).orElse(Duration.ZERO);

		return getSeconds(effectDuration);
	}

	private boolean canCastMoreBeforeSimulationEnds(AbilityId abilityId) {
		return getRemainingCooldown(abilityId) + getCastTime(abilityId) + getFullDuration(abilityId) < getRemainingSimulationDuration();
	}

	private Pattern getPattern(String regex) {
		var realRegex = "^" + regex.replace("*", ".*") + "$";

		return Pattern.compile(realRegex);
	}

	private static double getSeconds(AnyDuration anyDuration) {
		return switch (anyDuration) {
			case Duration duration -> duration.getSeconds();
			case InfiniteDuration() -> Double.MAX_VALUE;
		};
	}
}
