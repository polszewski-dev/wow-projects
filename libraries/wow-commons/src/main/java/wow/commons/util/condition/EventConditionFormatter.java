package wow.commons.util.condition;

import wow.commons.model.effect.component.EventCondition;

import java.util.List;
import java.util.Map;

import static wow.commons.model.effect.component.EventCondition.EmptyCondition;
import static wow.commons.model.effect.component.EventCondition.Operator;
import static wow.commons.model.effect.component.EventCondition.Operator.*;
import static wow.commons.util.condition.EventConditionParser.*;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public class EventConditionFormatter extends ConditionFormatter<EventCondition> {
	public static String formatCondition(EventCondition condition) {
		return new EventConditionFormatter().format(condition);
	}

	@Override
	protected String formatPrimitiveCondition(EventCondition condition) {
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

			case EmptyCondition() ->
					"";

			case HadNoCrit() ->
					getMiscCondition(condition);

			case HasCastTime() ->
					getMiscCondition(condition);

			case HasCastTimeUnder10Sec() ->
					getMiscCondition(condition);

			case HasManaCost() ->
					getMiscCondition(condition);

			case IsDirect() ->
					getMiscCondition(condition);

			case IsHostileSpell() ->
					getMiscCondition(condition);

			case IsNormalMeleeAttack() ->
					getMiscCondition(condition);

			case IsPeriodic() ->
					getMiscCondition(condition);

			case IsSpecialAttack() ->
					getMiscCondition(condition);

			case IsTargetingOthers() ->
					getMiscCondition(condition);

			case Operator ignored ->
					throw new IllegalArgumentException();

			case OwnerHasEffectCondition(var abilityId) ->
					OWNER_HAS_EFFECT_PREFIX + abilityId;

			case OwnerHealthBelowPct(var value) ->
					percentPrefix(OWNER_HEALTH_BELOW_PREFIX, value);

			case OwnerIsChannelingCondition(var abilityId) ->
					OWNER_IS_CHANNELING_PREFIX + abilityId;

			case PowerTypeCondition(var powerType) ->
					powerType.getKey();

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
		};
	}

	@Override
	protected boolean isBinaryOperator(EventCondition condition) {
		return condition instanceof BinaryOperator;
	}

	@Override
	protected boolean isOr(EventCondition condition) {
		return condition instanceof Or;
	}

	@Override
	protected boolean isAnd(EventCondition condition) {
		return condition instanceof And;
	}

	@Override
	protected boolean isComma(EventCondition condition) {
		return condition instanceof Comma;
	}

	@Override
	protected EventCondition getLeft(EventCondition operator) {
		return ((BinaryOperator) operator).left();
	}

	@Override
	protected EventCondition getRight(EventCondition operator) {
		return ((BinaryOperator) operator).right();
	}

	@Override
	protected List<EventCondition> getCommaConditions(EventCondition comma) {
		return ((Comma) comma).conditions();
	}

	private String getMiscCondition(EventCondition condition) {
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
