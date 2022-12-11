package wow.scraper.parsers.stats;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.repository.impl.parsers.excel.ComplexAttributeMapper;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.parser.ParserUtil;
import wow.scraper.parsers.setters.StatSetter;

import java.util.regex.Matcher;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class StatMatcher {
	private final StatPattern pattern;

	private StatMatcherParams params;
	private String[] parsedValues;

	public StatMatcher(StatPattern pattern) {
		this.pattern = pattern;
	}

	public boolean hasMatch() {
		return parsedValues != null;
	}

	public int getInt() {
		return getInt(1);
	}

	public double getDouble() {
		return getDouble(1);
	}

	public String getString() {
		return getString(1);
	}

	public int getInt(int i) {
		return Integer.parseInt(getString(i));
	}

	public double getDouble(int i) {
		return Double.parseDouble(getString(i));
	}

	public String getString(int i) {
		if (!hasMatch()) {
			throw new IllegalArgumentException();
		}
		if (i == 0) {
			return params.getOriginalLine();
		}
		return parsedValues[i - 1];
	}

	public String getParamType() {
		return pattern.getParams().getType();
	}

	public Attributes getParamStats() {
		String value = evalParams(pattern.getParams().getAmount());
		double amount = Double.parseDouble(value);
		return pattern.getParams().getStatsSupplier().getAttributes(amount);
	}

	public Duration getParamDuration() {
		String value = evalParams(pattern.getParams().getDuration());
		return Duration.parse(value);
	}

	public Duration getParamCooldown() {
		return params.getParsedCooldown();
	}

	public Percent getParamProcChance() {
		return params.getParsedProcChance();
	}

	public Duration getParamProcCooldown() {
		return params.getParsedProcCooldown();
	}

	public ComplexAttribute getExpression() {
		String value = evalParams(pattern.getParams().getExpression());
		return ComplexAttributeMapper.fromString(value);
	}

	private String evalParams(String pattern) {
		return ParserUtil.substituteParams(pattern, this::getString);
	}

	public boolean tryParse(StatMatcherParams params) {
		String lineToMatch = pattern.isLiteral() ? params.getOriginalLine() : params.getLine();
		Matcher matcher = pattern.getMatcher(lineToMatch);

		if (matcher == null) {
			return false;
		}

		this.params = params;
		this.parsedValues = ParserUtil.getMatchedGroups(matcher);
		return true;
	}

	public void setStat(AttributesBuilder stats) {
		if (hasMatch()) {
			for (StatSetter setter : pattern.getSetters()) {
				setter.set(stats, this);
			}
		}
	}

	@Override
	public String toString() {
		return params != null ? params.getOriginalLine() : null;
	}
}
