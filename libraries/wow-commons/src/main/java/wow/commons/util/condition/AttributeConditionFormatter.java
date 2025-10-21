package wow.commons.util.condition;

import wow.commons.model.attribute.AttributeCondition;

import java.util.List;
import java.util.Map;

import static wow.commons.model.attribute.AttributeCondition.*;
import static wow.commons.util.condition.AttributeConditionParser.MISC_CONDITIONS;
import static wow.commons.util.condition.AttributeConditionParser.OWNER_HEALTH_BELOW_PREFIX;

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

			case EffectCategoryCondition(var effectCategory) ->
					effectCategory.toString();

			case EmptyCondition() ->
					"";

			case HadCrit() ->
					getMiscCondition(condition);

			case HasCastTime() ->
					getMiscCondition(condition);

			case HasCastTimeUnder10Sec() ->
					getMiscCondition(condition);

			case HasHealingComponent() ->
					getMiscCondition(condition);

			case HasPet() ->
					getMiscCondition(condition);

			case IsDirect() ->
					getMiscCondition(condition);

			case IsInstantCast() ->
					getMiscCondition(condition);

			case MovementTypeCondition(var movementType) ->
					movementType.getName();

			case Operator ignored ->
					throw new IllegalArgumentException();

			case OwnerHealthBelowPct(var value) ->
					percentPrefix(OWNER_HEALTH_BELOW_PREFIX, value);

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
