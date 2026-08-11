package wow.character.util;

import wow.commons.model.spell.SpellTargetCondition;

import static wow.commons.model.spell.SpellTargetCondition.*;

/**
 * User: POlszewski
 * Date: 2025-11-30
 */
public final class SpellTargetConditionChecker {
	public static boolean check(SpellTargetCondition condition, SpellTargetConditionArgs args) {
		var caster = args.getCaster();
		var target = args.getTarget();

		return switch (condition) {
			case Empty() ->
					true;

			case HasEffect(var effectName) ->
					target.hasEffect(effectName);

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

			case IsClass(var characterClassId) ->
					target.getCharacterClassId() == characterClassId;

			case HasForm(var formType) ->
					target.getForm() == formType;

			case Friendly() ->
					caster.isFriendlyWith(target);

			case Hostile() ->
					caster.isHostileWith(target);

			case HasPet() ->
					caster.getActivePetType() != null;

			case HasActivePet(var petType) ->
					caster.getActivePetType() == petType;

			case SacrificedPet(var petType) ->
					args.getSacrificedPetType() == petType;

			case Or(var left, var right) ->
					check(left, args) || check(right, args);

			case And(var left, var right) ->
					check(left, args) && check(right, args);

			case Comma(var conditions) ->
					conditions.stream().anyMatch(x -> check(x, args));

			case Not(var innerCondition) ->
					!check(innerCondition, args);
		};
	}

	private SpellTargetConditionChecker() {}
}
