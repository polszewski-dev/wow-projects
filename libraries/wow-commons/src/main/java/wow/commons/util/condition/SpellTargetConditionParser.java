package wow.commons.util.condition;

import wow.commons.model.character.CreatureType;
import wow.commons.model.spell.SpellTargetCondition;

import java.util.List;
import java.util.function.DoubleFunction;

import static wow.commons.model.spell.SpellTargetCondition.*;

/**
 * User: POlszewski
 * Date: 2025-11-28
 */
public class SpellTargetConditionParser extends ConditionParser<SpellTargetCondition, String> {
	public static SpellTargetCondition parseCondition(String value) {
		return new SpellTargetConditionParser(value).parse();
	}

	private SpellTargetConditionParser(String value) {
		super(value);
	}

	@Override
	protected SpellTargetCondition orOperator(SpellTargetCondition left, SpellTargetCondition right) {
		return or(left, right);
	}

	@Override
	protected SpellTargetCondition andOperator(SpellTargetCondition left, SpellTargetCondition right) {
		return and(left, right);
	}

	@Override
	protected SpellTargetCondition commaOperator(List<SpellTargetCondition> conditions) {
		return comma(conditions);
	}

	@Override
	protected SpellTargetCondition getBasicCondition(String value) {
		if (value.isEmpty()) {
			return EMPTY;
		}

		var creatureType = CreatureType.tryParse(value);

		if (creatureType != null) {
			return SpellTargetCondition.of(creatureType);
		}

		var hasEffect = tryParseHasEffect(value);

		if (hasEffect != null) {
			return hasEffect;
		}

		if (FRIENDLY.equalsIgnoreCase(value)) {
			return SpellTargetCondition.FRIENDLY;
		}

		if (HOSTILE.equalsIgnoreCase(value)) {
			return SpellTargetCondition.HOSTILE;
		}

		throw new IllegalArgumentException("Unknown condition: " + value);
	}

	@Override
	protected SpellTargetCondition getEmptyCondition() {
		return EMPTY;
	}

	@Override
	protected SpellTargetCondition lessThanOperator(String left, String right) {
		return healthComparator(left, right, HealthPctLessThan::new);
	}

	@Override
	protected SpellTargetCondition lessThanOrEqualOperator(String left, String right) {
		return healthComparator(left, right, HealthPctLessThanOrEqual::new);
	}

	@Override
	protected SpellTargetCondition greaterThanOperator(String left, String right) {
		return healthComparator(left, right, HealthPctGreaterThan::new);
	}

	@Override
	protected SpellTargetCondition greaterThanOrEqualOperator(String left, String right) {
		return healthComparator(left, right, HealthPctGreaterThanOrEqual::new);
	}

	private SpellTargetCondition healthComparator(String left, String right, DoubleFunction<SpellTargetCondition> mapper) {
		if (HEALTH_PCT.equalsIgnoreCase(left)) {
			var pct = Double.parseDouble(right);
			return mapper.apply(pct);
		}

		throw new IllegalArgumentException("Can't parse: " + left);
	}

	@Override
	protected String getBasicExpression(String value) {
		return value;
	}

	@Override
	protected String getConstant(double value) {
		return value + "";
	}

	private HasEffect tryParseHasEffect(String value) {
		return parseAbilityIdArgument(value, HAS_EFFECT, HasEffect::new);
	}

	static final String HAS_EFFECT = "HasEffect";
	static final String HEALTH_PCT = "Health%";
	static final String FRIENDLY = "Friendly";
	static final String HOSTILE = "Hostile";
}
