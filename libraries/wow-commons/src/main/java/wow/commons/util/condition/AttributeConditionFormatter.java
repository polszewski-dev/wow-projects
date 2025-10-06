package wow.commons.util.condition;

import wow.commons.model.attribute.AttributeCondition;

import java.util.List;
import java.util.Map;

import static wow.commons.model.attribute.AttributeCondition.*;
import static wow.commons.util.condition.AttributeConditionParser.*;

/**
 * User: POlszewski
 * Date: 2025-10-05
 */
public class AttributeConditionFormatter extends ConditionFormatter<AttributeCondition> {
	public static String formatCondition(AttributeCondition condition) {
		return new AttributeConditionFormatter().format(condition);
	}

	@Override
	protected String formatPrimitiveCondition(AttributeCondition condition) {
		return switch (condition) {
			case AbilityCategoryCondition(var abilityCategory) ->
					abilityCategory.getName();
			case AbilityIdCondition(var abilityId) ->
					abilityId.name();
			case ActionTypeCondition(var actionType) ->
					actionType.getName();
			case CanCrit() ->
					getMiscCondition(condition);
			case DruidFormCondition(var druidFormType) ->
					druidFormType.getName();
			case EffectCategoryCondition(var effectCategory) ->
					effectCategory.toString();
			case EmptyCondition() ->
					"";
			case HadCrit() ->
					getMiscCondition(condition);
			case HadNoCrit() ->
					getMiscCondition(condition);
			case HasCastTime() ->
					getMiscCondition(condition);
			case HasCastTimeUnder10Sec() ->
					getMiscCondition(condition);
			case HasDamagingComponent() ->
					getMiscCondition(condition);
			case HasHealingComponent() ->
					getMiscCondition(condition);
			case HasManaCost() ->
					getMiscCondition(condition);
			case HasPet() ->
					getMiscCondition(condition);
			case IsDirect() ->
					getMiscCondition(condition);
			case IsHostileSpell() ->
					getMiscCondition(condition);
			case IsInstantCast() ->
					getMiscCondition(condition);
			case IsNormalMeleeAttack() ->
					getMiscCondition(condition);
			case IsPeriodic() ->
					getMiscCondition(condition);
			case IsSpecialAttack() ->
					getMiscCondition(condition);
			case IsTargetingOthers() ->
					getMiscCondition(condition);
			case MovementTypeCondition(var movementType) ->
					movementType.getName();
			case Operator ignored ->
					throw new IllegalArgumentException();
			case OwnerHasEffectCondition(var abilityId) ->
					OWNER_HAS_EFFECT_PREFIX + abilityId;
			case OwnerHealthBelowPct(var value) ->
					percentPrefix(OWNER_HEALTH_BELOW_PREFIX, value);
			case OwnerIsChannelingCondition(var abilityId) ->
					OWNER_IS_CHANNELING_PREFIX + abilityId;
			case PetTypeCondition(var petType) ->
					petType.toString();
			case PowerTypeCondition(var powerType) ->
					powerType.getKey();
			case ProfessionCondition(var professionId) ->
					professionId.getName();
			case SpellSchoolCondition(var spellSchool) ->
					spellSchool.getName();
			case TalentTreeCondition(var talentTree) ->
					talentTree.getName();
			case TargetClassCondition(var characterClassId) ->
					TARGET_CLASS_PREFIX + characterClassId;
			case TargetHealthBelowPct(var value) ->
					percentPrefix(TARGET_HEALTH_BELOW_PREFIX, value);
			case TargetTypeCondition(var creatureType) ->
					creatureType.getName();
			case WeaponTypeCondition(var weaponType) ->
					weaponType.getKey();
		};
	}

	@Override
	protected boolean isBinaryOperator(AttributeCondition condition) {
		return condition instanceof BinaryOperator;
	}

	@Override
	protected boolean isOr(AttributeCondition condition) {
		return condition instanceof Or;
	}

	@Override
	protected boolean isAnd(AttributeCondition condition) {
		return condition instanceof And;
	}

	@Override
	protected boolean isComma(AttributeCondition condition) {
		return condition instanceof Comma;
	}

	@Override
	protected AttributeCondition getLeft(AttributeCondition operator) {
		return ((BinaryOperator) operator).left();
	}

	@Override
	protected AttributeCondition getRight(AttributeCondition operator) {
		return ((BinaryOperator) operator).right();
	}

	@Override
	protected List<AttributeCondition> getCommaConditions(AttributeCondition comma) {
		return ((Comma) comma).conditions();
	}

	private String getMiscCondition(AttributeCondition condition) {
		return MISC_CONDITIONS.entrySet().stream()
				.filter(e -> e.getValue().equals(condition))
				.map(Map.Entry::getKey)
				.findAny()
				.orElseThrow();
	}

	private String percentPrefix(String prefix, int value) {
		return prefix + value + "%";
	}
}
