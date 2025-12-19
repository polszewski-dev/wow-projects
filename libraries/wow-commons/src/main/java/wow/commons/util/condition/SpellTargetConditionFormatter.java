package wow.commons.util.condition;

import wow.commons.model.spell.SpellTargetCondition;

import java.util.List;

import static wow.commons.model.spell.SpellTargetCondition.*;
import static wow.commons.util.condition.SpellTargetConditionParser.FRIENDLY;
import static wow.commons.util.condition.SpellTargetConditionParser.HAS_PET;
import static wow.commons.util.condition.SpellTargetConditionParser.HOSTILE;
import static wow.commons.util.condition.SpellTargetConditionParser.*;

/**
 * User: POlszewski
 * Date: 2025-11-28
 */
public class SpellTargetConditionFormatter extends ConditionFormatter<SpellTargetCondition> {
	public static String formatCondition(SpellTargetCondition condition) {
		return new SpellTargetConditionFormatter().format(condition);
	}

	@Override
	protected String formatPrimitiveCondition(SpellTargetCondition condition) {
		return switch (condition) {
			case Empty() -> "";

			case Operator ignored ->
					throw new IllegalArgumentException();

			case HasEffect(var abilityId) ->
					formatFunction(HAS_EFFECT, abilityId);

			case HealthPctLessThan(var value) ->
					formatOperator(HEALTH_PCT, "<", value);

			case HealthPctLessThanOrEqual(var value) ->
					formatOperator(HEALTH_PCT, "<=", value);

			case HealthPctGreaterThan(var value) ->
					formatOperator(HEALTH_PCT, ">", value);

			case HealthPctGreaterThanOrEqual(var value) ->
					formatOperator(HEALTH_PCT, ">=", value);

			case IsCreatureType(var creatureType) ->
					creatureType.getName();

			case IsClass(var classId) ->
					CLASS + " = " + classId;

			case HasDruidForm(var druidFormType) ->
					druidFormType.getName();

			case Friendly() ->
					FRIENDLY;

			case Hostile() ->
					HOSTILE;

			case HasPet() ->
					HAS_PET;

			case SacrificedPet(var petType) ->
					formatFunction(SACRIFICED, petType);
		};
	}

	@Override
	protected boolean isBinaryOperator(SpellTargetCondition condition) {
		return condition instanceof BinaryOperator;
	}

	@Override
	protected SpellTargetCondition getLeft(SpellTargetCondition operator) {
		return ((BinaryOperator) operator).left();
	}

	@Override
	protected SpellTargetCondition getRight(SpellTargetCondition operator) {
		return ((BinaryOperator) operator).right();
	}

	@Override
	protected SpellTargetCondition getNotCondition(SpellTargetCondition operator) {
		return ((Not) operator).condition();
	}

	@Override
	protected boolean isOr(SpellTargetCondition condition) {
		return condition instanceof Or;
	}

	@Override
	protected boolean isAnd(SpellTargetCondition condition) {
		return condition instanceof And;
	}

	@Override
	protected boolean isComma(SpellTargetCondition condition) {
		return condition instanceof Comma;
	}

	@Override
	protected boolean isNot(SpellTargetCondition condition) {
		return condition instanceof Not;
	}

	@Override
	protected List<SpellTargetCondition> getCommaConditions(SpellTargetCondition comma) {
		return ((Comma) comma).conditions();
	}
}
