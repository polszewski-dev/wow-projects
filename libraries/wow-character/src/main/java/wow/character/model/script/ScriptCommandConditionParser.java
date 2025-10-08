package wow.character.model.script;

import wow.commons.model.spell.AbilityId;
import wow.commons.util.condition.ConditionParser;
import wow.commons.util.parser.ParserUtil;

import java.util.function.Function;

import static wow.character.model.script.ScriptCommandCondition.*;

/**
 * User: POlszewski
 * Date: 2025-09-17
 */
public class ScriptCommandConditionParser extends ConditionParser<ScriptCommandCondition, Expression> {
	public ScriptCommandConditionParser(String value) {
		super(value);
	}

	@Override
	protected ScriptCommandCondition orOperator(ScriptCommandCondition left, ScriptCommandCondition right) {
		return new Or(left, right);
	}

	@Override
	protected ScriptCommandCondition andOperator(ScriptCommandCondition left, ScriptCommandCondition right) {
		return new And(left, right);
	}

	@Override
	protected ScriptCommandCondition notOperator(ScriptCommandCondition condition) {
		return new Not(condition);
	}

	@Override
	protected ScriptCommandCondition lessThanOperator(Expression left, Expression right) {
		return new LessThan(left, right);
	}

	@Override
	protected ScriptCommandCondition lessThanOrEqualOperator(Expression left, Expression right) {
		return new LessThanOrEqual(left, right);
	}

	@Override
	protected ScriptCommandCondition greaterThanOperator(Expression left, Expression right) {
		return new GreaterThan(left, right);
	}

	@Override
	protected ScriptCommandCondition greaterThanOrEqualOperator(Expression left, Expression right) {
		return new GreaterThanOrEqual(left, right);
	}

	@Override
	protected ScriptCommandCondition getBasicCondition(String value) {
		var casterHasEffect = parseCasterHasEffect(value);

		if (casterHasEffect != null) {
			return casterHasEffect;
		}

		var targetHasEffect = parseTargetHasEffect(value);

		if (targetHasEffect != null) {
			return targetHasEffect;
		}

		var canCastMoreBeforeSimulationEnds = parseCanCastMoreBeforeSimulationEnds(value);

		if (canCastMoreBeforeSimulationEnds != null) {
			return canCastMoreBeforeSimulationEnds;
		}

		throw new IllegalArgumentException("Invalid condition: " + value);
	}

	@Override
	protected Expression getBasicExpression(String value) {
		if (value.equals("Health")) {
			return new CasterHealth();
		}

		if (value.equals("Mana")) {
			return new CasterMana();
		}

		if (value.equals("Health%")) {
			return new CasterHealthPct();
		}

		if (value.equals("Mana%")) {
			return new CasterManaPct();
		}

		if (value.equals("Target.Health%")) {
			return new TargetHealthPct();
		}

		if (value.equals("Target.Mana%")) {
			return new TargetManaPct();
		}

		if (value.equals("FullDuration")) {
			return new FullDuration();
		}

		if (value.equals("RemainingSimulationDuration")) {
			return new RemainingSimulationDuration();
		}

		var remainingCooldown = parseRemainingCooldown(value);

		if (remainingCooldown != null) {
			return remainingCooldown;
		}

		return null;
	}

	@Override
	protected Constant getConstant(double value) {
		return new Constant(value);
	}

	private CasterHasEffect parseCasterHasEffect(String value) {
		return extractSingleValue(value, "Caster\\.HasEffect\\((.+)\\)", CasterHasEffect::new);
	}

	private TargetHasEffect parseTargetHasEffect(String value) {
		return extractSingleValue(value, "Target\\.HasEffect\\((.+)\\)", TargetHasEffect::new);
	}

	private CanCastMoreBeforeSimulationEnds parseCanCastMoreBeforeSimulationEnds(String value) {
		return extractSingleValue(value, "CanCastMoreBeforeSimulationEnds\\((.+)\\)", x -> new CanCastMoreBeforeSimulationEnds(AbilityId.of(x)));
	}

	private RemainingCooldown parseRemainingCooldown(String value) {
		return extractSingleValue(value, "RemainingCooldown\\((.+)\\)", x -> new RemainingCooldown(AbilityId.of(x)));
	}

	private <T> T extractSingleValue(String value, String regex, Function<String, T> mapper) {
		var parseResult = ParserUtil.parseMultipleValues(regex, value);

		if (parseResult.isEmpty()) {
			return null;
		}

		var groupValue = parseResult.get(0).trim();

		if (groupValue.isEmpty()) {
			return null;
		}

		return mapper.apply(groupValue);
	}

	@Override
	protected ScriptCommandCondition getEmptyCondition() {
		return EMPTY;
	}
}
