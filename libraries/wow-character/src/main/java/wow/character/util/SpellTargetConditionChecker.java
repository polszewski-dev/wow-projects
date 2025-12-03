package wow.character.util;

import wow.character.model.character.Character;
import wow.commons.model.spell.SpellTargetCondition;

import static wow.commons.model.spell.SpellTargetCondition.*;

/**
 * User: POlszewski
 * Date: 2025-11-30
 */
public class SpellTargetConditionChecker {
	public static boolean check(SpellTargetCondition condition, Character target) {
		return switch (condition) {
			case Empty() ->
					true;

			case HasEffect(var abilityId) ->
					target.hasEffect(abilityId);

			case HealthPctLessThan(var value) ->
					target.getHealthPct().value() < value;

			case HealthPctLessThanOrEqual(var value) ->
					target.getHealthPct().value() <= value;

			case HealthPctGreaterThan(var value) ->
					target.getHealthPct().value() > value;

			case HealthPctGreaterThanOrEqual(var value) ->
					target.getHealthPct().value() >= value;

			case IsCreatureType(var creatureType) ->
					target.getCreatureType() == creatureType;

			case Or(var left, var right) ->
					check(left, target) || check(right, target);

			case And(var left, var right) ->
					check(left, target) && check(right, target);

			case Comma(var conditions) ->
					conditions.stream().anyMatch(x -> check(x, target));
		};
	}
}
