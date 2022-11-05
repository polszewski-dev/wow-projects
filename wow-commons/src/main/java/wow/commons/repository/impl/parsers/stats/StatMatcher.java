package wow.commons.repository.impl.parsers.stats;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.repository.impl.parsers.setters.StatSetter;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.ParserUtil;

import java.util.regex.Matcher;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class StatMatcher {
	private final StatPattern pattern;

	private String matchedLine;
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
			return matchedLine;
		}
		return parsedValues[i - 1];
	}

	public String getParamType() {
		return pattern.getParams().getType();
	}

	public Attributes getParamStats() {
		String value = evalParams(pattern.getParams().getAmount());
		int amount = Integer.parseInt(value);
		return pattern.getParams().getStatsSupplier().getAttributes(amount);
	}

	public Duration getParamDuration() {
		String value = evalParams(pattern.getParams().getDuration());
		return Duration.parse(value);
	}

	public Duration getParamCooldown() {
		String value = evalParams(pattern.getParams().getCooldown());
		return CooldownParser.parseCooldown(value);
	}

	public Percent getParamProcChance() {
		String value = evalParams(pattern.getParams().getProcChance());
		return value != null ? Percent.of(Double.parseDouble(value)) : null;
	}

	public Duration getParamProcCooldown() {
		String value = evalParams(pattern.getParams().getProcCooldown());
		return Duration.parse(value);
	}

	private String evalParams(String pattern) {
		return ParserUtil.substituteParams(pattern, this::getString);
	}

	public boolean tryParse(String line) {
		Matcher matcher = pattern.getMatcher(line);
		if (matcher == null) {
			return false;
		}
		if (pattern.canHaveUsefulMatch() && matchedLine != null) {
			throw new IllegalStateException("Have already parsed values: " + line);
		}
		this.matchedLine = line;
		this.parsedValues = new String[matcher.groupCount()];
		for (int i = 1; i <= matcher.groupCount(); ++i) {
			String group = matcher.group(i);
			parsedValues[i - 1] = group;
		}
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
		return matchedLine;
	}
}
