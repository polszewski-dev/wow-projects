package wow.character.util;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wow.character.model.character.Character;
import wow.commons.model.character.PetType;
import wow.commons.model.spell.SpellTargetCondition;

import static wow.commons.model.spell.SpellTargetCondition.*;

/**
 * User: POlszewski
 * Date: 2025-11-30
 */
@RequiredArgsConstructor
public final class SpellTargetConditionChecker {
	private final Character caster;
	private final Character target;
	@Setter
	private PetType sacrificedPetType;

	public boolean check(SpellTargetCondition condition) {
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
					sacrificedPetType == petType;

			case Or(var left, var right) ->
					check(left) || check(right);

			case And(var left, var right) ->
					check(left) && check(right);

			case Comma(var conditions) ->
					conditions.stream().anyMatch(this::check);

			case Not(var innerCondition) ->
					!check(innerCondition);
		};
	}
}
