package wow.simulator.script.prediction;

import wow.character.model.script.ScriptCommandCondition;
import wow.commons.model.spell.Ability;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.Unit;

import java.util.regex.Pattern;

import static wow.character.model.script.ScriptCommandCondition.*;

/**
 * User: POlszewski
 * Date: 2025-10-07
 */
public class ScriptConditionChecker {
	private final PredictedState currentState;
	private final Unit caster;
	private final Ability ability;
	private final Unit primaryTarget;

	public ScriptConditionChecker(PredictedState currentState, Unit caster, Ability ability, Unit primaryTarget) {
		this.currentState = currentState;
		this.caster = caster;
		this.ability = ability;
		this.primaryTarget = primaryTarget;
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
					currentState.casterHasEffect(getPattern(effectNamePattern));

			case TargetHasEffect(var effectNamePattern) ->
					currentState.targetHasEffect(getPattern(effectNamePattern));

			case CanCastMoreBeforeSimulationEnds(var abilityId) ->
					currentState.canCastMoreBeforeSimulationEnds(abilityId, primaryTarget);

			case AssetEnabled(var assetName) ->
					caster instanceof Player player && player.getAssets().has(assetName);
		};
	}

	private double eval(Expression expression) {
		return switch (expression) {
			case Constant(var value) ->
					value;

			case CasterHealth() ->
					currentState.getCasterHealth();

			case CasterMana() ->
					currentState.getCasterMana();

			case CasterHealthPct() ->
					currentState.getCasterHealthPct();

			case CasterManaPct() ->
					currentState.getCasterManaPct();

			case TargetHealthPct() ->
					currentState.getTargetHealthPct();

			case TargetManaPct() ->
					currentState.getTargetManaPct();

			case FocusHealthPct() ->
					currentState.getFocusHealthPct();

			case FocusManaPct() ->
					currentState.getFocusManaPct();

			case PetHealthPct() ->
					currentState.getPetHealthPct();

			case PetManaPct() ->
					currentState.getPetManaPct();

			case MasterHealthPct() ->
					currentState.getMasterHealthPct();

			case MasterManaPct() ->
					currentState.getMasterManaPct();

			case FullDuration() ->
					currentState.getFullDuration(ability, primaryTarget);

			case RemainingCooldown(var abilityId) ->
					currentState.getRemainingCooldown(abilityId);

			case RemainingSimulationDuration() ->
					currentState.getRemainingSimulationDuration();

			case TargetNumStacksOfAny(var effectName) ->
					currentState.getNumStacksOfAnyOnTarget(effectName);
			
			case TargetRemainingEffectDurationOfAny(var effectName) ->
					currentState.getRemainingEffectDurationOfAnyOnTarget(effectName);
		};
	}

	private Pattern getPattern(String regex) {
		var realRegex = "^" + regex.replace("*", ".*") + "$";

		return Pattern.compile(realRegex);
	}
}
