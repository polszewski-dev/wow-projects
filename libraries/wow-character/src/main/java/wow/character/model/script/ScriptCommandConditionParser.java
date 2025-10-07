package wow.character.model.script;

import wow.commons.util.condition.ConditionParser;
import wow.commons.util.parser.ParserUtil;

import java.util.function.Function;

import static wow.character.model.script.ScriptCommandCondition.*;

/**
 * User: POlszewski
 * Date: 2025-09-17
 */
public class ScriptCommandConditionParser extends ConditionParser<ScriptCommandCondition> {
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
	protected ScriptCommandCondition getBasicCondition(String value) {
		var casterHasEffect = parseCasterHasEffect(value);

		if (casterHasEffect != null) {
			return casterHasEffect;
		}

		var targetHasEffect = parseTargetHasEffect(value);

		if (targetHasEffect != null) {
			return targetHasEffect;
		}

		throw new IllegalArgumentException("Invalid condition: " + value);
	}

	private CasterHasEffect parseCasterHasEffect(String value) {
		return extractSingleValue(value, "Caster\\.HasEffect\\((.*)\\)", CasterHasEffect::new);
	}

	private TargetHasEffect parseTargetHasEffect(String value) {
		return extractSingleValue(value, "Target\\.HasEffect\\((.*)\\)", TargetHasEffect::new);
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
