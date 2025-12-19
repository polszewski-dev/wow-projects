package wow.character.util;

import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.ClassAbility;

import static wow.commons.model.attribute.AttributeCondition.*;


/**
 * User: POlszewski
 * Date: 29.09.2024
 */
public final class AttributeConditionChecker {
	public static boolean check(AttributeCondition condition, AttributeConditionArgs args) {
		return switch (condition) {
			case AbilityCategoryCondition(var abilityCategory) ->
					args.getSpell() instanceof Ability a &&
					a.getCategory() == abilityCategory;

			case AbilityIdCondition(var abilityId) ->
					args.getSpell() instanceof Ability a &&
					a.getAbilityId().equals(abilityId);

			case ActionTypeCondition(var actionType) ->
					args.getActionType() == actionType;

			case EffectCategoryCondition(var effectCategory) ->
					args.getAppliedEffect() != null &&
					args.getAppliedEffect().getCategory() == effectCategory;

			case EffectNameCondition(var effectName) ->
					args.getAppliedEffect() != null &&
					args.getAppliedEffect().getName().equals(effectName);

			case EmptyCondition() ->
					true;

			case HadCrit() ->
					args.isHadCrit();

			case HasCastTime() ->
					args.getSpell() instanceof Ability a &&
					a.getCastTime().isPositive();

			case HasCastTimeUnder10Sec() ->
					args.getSpell() instanceof Ability a &&
					a.getCastTime().getSeconds() < 10;

			case HasHealingComponent() ->
					args.getSpell() != null &&
					args.getSpell().hasHealingComponent();

			case HasPet() ->
					args.getCaster().getActivePetType() != null;

			case IsDirect() ->
					args.isDirect();

			case IsPeriodic() ->
					args.isPeriodic();

			case IsInstantCast() ->
					args.getSpell() instanceof Ability a &&
					a.getCastTime().isZero();

			case MovementTypeCondition(var movementType) ->
					args.getCaster().getMovementType() == movementType;

			case Operator operator ->
					checkConditionOperator(operator, args);

			case OwnerHasEffect(var effectName) ->
					args.getCaster().hasEffect(effectName);

			case OwnerHealthPctLessThan(var value) ->
					args.getCaster().getHealthPct().value() < value;

			case PetTypeCondition(var petType) ->
					args.getCaster().getActivePetType() == petType;

			case PowerTypeCondition(var powerType) ->
					args.getPowerType() == powerType;

			case ProfessionCondition(var professionId) ->
					args.getCaster().hasProfession(professionId);

			case SpellSchoolCondition(var spellSchool) ->
					args.getSpellSchool() == spellSchool;

			case TargetHealthPctLessThan(var value) ->
					args.getTarget() != null &&
					args.getTarget().getHealthPct().value() < value;

			case TalentTreeCondition(var talentTree) ->
					args.getSpell() instanceof ClassAbility a &&
					a.getTalentTree() == talentTree;

			case TargetTypeCondition(var creatureType) ->
					args.getTarget() != null && args.getTarget().getCreatureType() == creatureType;

			case WeaponTypeCondition(var weaponType) ->
					args.getWeaponType() == weaponType;
		};
	}

	private static boolean checkConditionOperator(Operator operator, AttributeConditionArgs args) {
		return switch (operator) {
			case And(var left, var right) ->
					check(left, args) && check(right, args);

			case Or(var left, var right) ->
					check(left, args) || check(right, args);

			case Comma comma ->
					checkComma(comma, args);
		};
	}

	private static boolean checkComma(Comma comma, AttributeConditionArgs args) {
		for (var condition : comma.conditions()) {
			if (check(condition, args)) {
				return true;
			}
		}
		return false;
	}

	private AttributeConditionChecker() {}
}
