package wow.character.util;

import wow.commons.model.attribute.PowerType;
import wow.commons.model.attribute.condition.*;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.ClassAbility;
import wow.commons.model.spell.ResourceType;

/**
 * User: POlszewski
 * Date: 29.09.2024
 */
public final class AttributeConditionChecker {
	public static boolean check(AttributeCondition condition, AttributeConditionArgs args) {
		return switch (condition) {
			case AbilityCategoryCondition c ->
					args.getSpell() instanceof Ability a &&
					a.getCategory() == c.abilityCategory();
			case AbilityIdCondition c ->
					args.getSpell() instanceof Ability a &&
					a.getAbilityId() == c.abilityId();
			case ConditionOperator c ->
					checkConditionOperator(c, args);
			case DruidFormCondition c ->
					args.getCaster().getDruidForm() == c.type();
			case EffectCategoryCondition c ->
					args.getSpell() != null &&
					args.getSpell().getAppliedEffect() != null &&
					args.getSpell().getAppliedEffect().getCategory() == c.type();
			case EmptyCondition ignored ->
					true;
			case MiscCondition c ->
					checkMiscCondition(c, args);
			case MovementTypeCondition c ->
					args.getCaster().getMovementType() == c.type();
			case OwnerHasEffectCondition c ->
					args.getCaster().hasEffect(c.abilityId());
			case OwnerIsChannelingCondition c ->
					args.getSpell() instanceof Ability a &&
					a.isChanneled() &&
					a.getAbilityId() == c.abilityId();
			case PetTypeCondition c ->
					args.getCaster().getActivePetType() == c.petType();
			case ProfessionCondition c ->
					args.getCaster().hasProfession(c.professionId());
			case SpellSchoolCondition c ->
					args.getSpellSchool() == c.spellSchool();
			case TalentTreeCondition c ->
					args.getSpell() instanceof ClassAbility a &&
					a.getTalentTree() == c.talentTree();
			case TargetClassCondition c ->
					args.getTarget() != null && args.getTarget().getCharacterClassId() == c.characterClassId();
			case TargetTypeCondition c ->
					args.getTarget() != null && args.getTarget().getCreatureType() == c.creatureType();
			case WeaponTypeCondition c ->
					args.getWeaponType() == c.weaponType();
		};
	}

	private static boolean checkConditionOperator(ConditionOperator operator, AttributeConditionArgs args) {
		return switch (operator) {
			case ConditionOperator.BinaryConditionOperator binaryOperator ->
					checkBinaryOperator(binaryOperator, args);
		};
	}

	private static boolean checkBinaryOperator(ConditionOperator.BinaryConditionOperator operator, AttributeConditionArgs args) {
		var left = operator.left();
		var right = operator.right();

		return switch (operator) {
			case ConditionOperator.And ignored ->
					check(left, args) && check(right, args);
			case ConditionOperator.Comma ignored ->
					check(left, args) || check(right, args);
			case ConditionOperator.Or ignored ->
					check(left, args) || check(right, args);
		};
	}

	private static boolean checkMiscCondition(MiscCondition condition, AttributeConditionArgs args) {
		return switch (condition) {
			case SPELL ->
					args.getActionType() == ActionType.SPELL;
			case PHYSICAL ->
					args.getActionType() == ActionType.PHYSICAL;
			case SPELL_DAMAGE ->
					args.getPowerType() == PowerType.SPELL_DAMAGE;
			case HEALING ->
					args.getPowerType() == PowerType.HEALING;
			case MELEE ->
					args.getPowerType() == PowerType.MELEE;
			case RANGED ->
					args.getPowerType() == PowerType.RANGED;
			case WEAPON ->
					args.getPowerType() == PowerType.WEAPON;
			case DIRECT ->
					args.isDirect();
			case PERIODIC ->
					args.isPeriodic();
			case HAS_DAMAGING_COMPONENT ->
					args.getSpell() != null &&
					args.getSpell().hasDamagingComponent();
			case HAS_HEALING_COMPONENT ->
					args.getSpell() != null &&
					args.getSpell().hasHealingComponent();
			case HOSTILE_SPELL ->
					args.isHostileSpell();
			case NORMAL_MELEE_ATTACK ->
					args.isNormalMeleeAttack();
			case SPECIAL_ATTACK ->
					args.isSpecialAttack();
			case HAS_MANA_COST ->
					args.getSpell() instanceof Ability a &&
					a.getCost() != null &&
					a.getCost().resourceType() == ResourceType.MANA;
			case HAS_CAST_TIME ->
					args.getSpell() instanceof Ability a &&
					a.getCastTime().isPositive();
			case IS_INSTANT_CAST ->
					args.getSpell() instanceof Ability a &&
					a.getCastTime().isZero();
			case HAS_CAST_TIME_UNDER_10_SEC ->
					args.getSpell() instanceof Ability a &&
					a.getCastTime().getSeconds() < 10;
			case CAN_CRIT ->
					args.isCanCrit();
			case HAD_CRITICAL ->
					args.isHadCrit();
			case HAD_NO_CRITICAL ->
					args.isCanCrit() && !args.isHadCrit();
			case HAS_PET ->
					args.getCaster().getActivePetType() != null;
			case TARGETING_OTHERS ->
					args.getTarget() != null &&
					args.getTarget() != args.getCaster();
			case OWNER_HEALTH_BELOW_20_PCT ->
					args.getCaster().getHealthPct().value() < 20;
			case OWNER_HEALTH_BELOW_35_PCT ->
					args.getCaster().getHealthPct().value() < 35;
			case OWNER_HEALTH_BELOW_40_PCT ->
					args.getCaster().getHealthPct().value() < 40;
			case OWNER_HEALTH_BELOW_70_PCT ->
					args.getCaster().getHealthPct().value() < 70;
			case TARGET_HEALTH_BELOW_50_PCT ->
					args.getTarget().getHealthPct().value() < 50;
		};
	}

	private AttributeConditionChecker() {}
}
