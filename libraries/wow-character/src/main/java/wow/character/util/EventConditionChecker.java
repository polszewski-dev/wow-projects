package wow.character.util;

import wow.commons.model.effect.component.EventCondition;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.ClassAbility;

import static wow.commons.model.effect.component.EventCondition.*;
import static wow.commons.model.spell.ResourceType.MANA;


/**
 * User: POlszewski
 * Date: 29.09.2024
 */
public final class EventConditionChecker {
	public static boolean check(EventCondition condition, EventConditionArgs args) {
		return switch (condition) {
			case AbilityCategoryCondition(var abilityCategory) ->
					args.getSpell() instanceof Ability a &&
					a.getCategory() == abilityCategory;

			case AbilityIdCondition(var abilityId) ->
					args.getSpell() instanceof Ability a &&
					a.getAbilityId().equals(abilityId);

			case ActionTypeCondition(var actionType) ->
					args.getActionType() == actionType;

			case CanCrit() ->
					args.isCanCrit();

			case DruidFormCondition(var druidFormType) ->
					args.getCaster().getDruidForm() == druidFormType;

			case EmptyCondition() ->
					true;

			case HadNoCrit() ->
					args.isCanCrit() && !args.isHadCrit();

			case HadCrit() ->
					args.isCanCrit() && args.isHadCrit();

			case HasCastTime() ->
					args.getSpell() instanceof Ability a &&
					a.getCastTime().isPositive();

			case HasCastTimeUnder10Sec() ->
					args.getSpell() instanceof Ability a &&
					a.getCastTime().getSeconds() > 0 &&
					a.getCastTime().getSeconds() < 10;

			case HasManaCost() ->
					args.getSpell() instanceof Ability a &&
					a.getCost() != null &&
					a.getCost().resourceType() == MANA;

			case IsDirect() ->
					args.isDirect();

			case IsHostileSpell() ->
					args.isHostileSpell();

			case IsNormalMeleeAttack() ->
					args.isNormalMeleeAttack();

			case IsPeriodic() ->
					args.isPeriodic();

			case IsSpecialAttack() ->
					args.isSpecialAttack();

			case IsTargetingOthers() ->
					args.getTarget() != null &&
					args.getTarget() != args.getCaster();

			case Operator operator ->
					checkConditionOperator(operator, args);

			case OwnerHasEffectCondition(var abilityId) ->
					args.getCaster().hasEffect(abilityId);

			case OwnerHealthBelowPct(var value) ->
					args.getCaster().getHealthPct().value() < value;

			case OwnerIsChannelingCondition(var abilityId) ->
					args.getSpell() instanceof Ability a &&
					a.isChanneled() &&
					a.getAbilityId().equals(abilityId);

			case PowerTypeCondition(var powerType) ->
					args.getPowerType() == powerType;

			case SpellSchoolCondition(var spellSchool) ->
					args.getSpellSchool() == spellSchool;

			case TalentTreeCondition(var talentTree) ->
					args.getSpell() instanceof ClassAbility a &&
					a.getTalentTree() == talentTree;

			case TargetClassCondition(var characterClassId) ->
					args.getTarget() != null && args.getTarget().getCharacterClassId() == characterClassId;

			case TargetHealthBelowPct(var value) ->
					args.getTarget().getHealthPct().value() < value;

			case TargetTypeCondition(var creatureType) ->
					args.getTarget() != null && args.getTarget().getCreatureType() == creatureType;
		};
	}

	private static boolean checkConditionOperator(Operator operator, EventConditionArgs args) {
		return switch (operator) {
			case And(var left, var right) ->
					check(left, args) && check(right, args);

			case Or(var left, var right) ->
					check(left, args) || check(right, args);

			case Comma comma ->
					checkComma(comma, args);
		};
	}

	private static boolean checkComma(Comma comma, EventConditionArgs args) {
		for (var condition : comma.conditions()) {
			if (check(condition, args)) {
				return true;
			}
		}
		return false;
	}

	private EventConditionChecker() {}
}
