package wow.simulator.script.command;

import lombok.RequiredArgsConstructor;
import wow.character.model.script.ScriptCommandCondition;
import wow.commons.model.Duration;
import wow.commons.model.InfiniteDuration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.unit.Unit;

import java.util.regex.Pattern;

import static wow.character.model.script.ScriptCommandCondition.*;

/**
 * User: POlszewski
 * Date: 2025-10-07
 */
@RequiredArgsConstructor
public class ScriptConditionChecker {
	private final Unit caster;
	private final Ability ability;
	private final Unit target;

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

			case FullDuration() ->
					getFullDuration(ability.getAbilityId());

			case RemainingCooldown(var abilityId) ->
					getRemainingCooldown(abilityId);

			case RemainingSimulationDuration() ->
					getRemainingSimulationDuration();
		};
	}

	private double getCastTime(AbilityId abilityId) {
		var snapshot = caster.getSpellCastSnapshot(abilityId);

		return snapshot.getCastTime();
	}

	private double getFullDuration(AbilityId abilityId) {
		var snapshot = caster.getEffectDurationSnapshot(abilityId, target);

		return switch (snapshot.getDuration()) {
			case Duration duration -> duration.getSeconds();
			case InfiniteDuration() -> Double.MAX_VALUE;
		};
	}

	private double getRemainingCooldown(AbilityId abilityId) {
		return caster.getRemainingCooldown(abilityId).getSeconds();
	}

	private double getRemainingSimulationDuration() {
		return caster.getSimulation().getRemainingTime().getSeconds();
	}

	private boolean canCastMoreBeforeSimulationEnds(AbilityId abilityId) {
		return getRemainingCooldown(abilityId) + getCastTime(abilityId) + getFullDuration(abilityId) < getRemainingSimulationDuration();
	}

	private Pattern getPattern(String regex) {
		var realRegex = "^" + regex.replace("*", ".*") + "$";

		return Pattern.compile(realRegex);
	}
}
