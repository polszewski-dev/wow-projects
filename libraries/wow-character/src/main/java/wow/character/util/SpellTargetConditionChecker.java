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

			case HealthAtMostPct(var value) ->
					target.getHealthPct().value() <= value;

			case IsCreatureType(var creatureType) ->
					target.getCreatureType() == creatureType;

			case Comma(var conditions) ->
					conditions.stream().anyMatch(x -> check(x, target));
		};
	}
}
