package wow.commons.repository.impl.parsers.stats;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.repository.impl.parsers.setters.StatSetter;
import wow.commons.util.AttributesBuilder;

import java.util.function.IntConsumer;
import java.util.regex.Matcher;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class StatMatcher {
	private final StatPattern pattern;

	private String matchedLine;
	private String[] parsedValues;
	private boolean matchUsed;

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
		this.matchUsed = true;
		if (i == 0) {
			return matchedLine;
		}
		return parsedValues[i - 1];
	}

	public String getParamType() {
		return pattern.getParams().getType();
	}

	public Attributes getParamStats(Integer amount) {
		return pattern.getParams().getStatsSupplier().getAttributes(amount);
	}

	public Integer getParamAmount() {
		return evalParam(pattern.getParams().getAmount());
	}

	public Duration getParamDuration() {
		return Duration.seconds(evalParam(pattern.getParams().getDuration()));
	}

	public Duration getParamCooldown() {
		String value = evalParamString(pattern.getParams().getCooldown());
		return Duration.seconds(CooldownParser.parseCooldown(value));
	}

	public Percent getParamProcChance() {
		return evalParamPercent(pattern.getParams().getProcChance());
	}

	public Duration getParamProcCooldown() {
		return Duration.seconds(evalParam(pattern.getParams().getProcCooldown()));
	}

	private Integer evalParam(String paramValue) {
		if (paramValue == null) {
			return null;
		}
		if (paramValue.startsWith("$")) {
			int groupNo = Integer.parseInt(paramValue.substring(1));
			return getInt(groupNo);
		}
		return Integer.valueOf(paramValue);
	}

	private Percent evalParamPercent(String paramValue) {
		Integer value = evalParam(paramValue);
		return value != null ? Percent.of(value) : null;
	}

	private String evalParamString(String paramValue) {
		if (paramValue == null) {
			return null;
		}
		if (paramValue.startsWith("$")) {
			int groupNo = Integer.parseInt(paramValue.substring(1));
			return getString(groupNo);
		}
		return paramValue;
	}

	public String getMatchedLine() {
		return matchedLine;
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

	private void setItemStats(AttributesBuilder itemStats) {
		for (StatSetter setter : pattern.getSetters()) {
			setter.set(itemStats, this);
		}
	}

	public Attributes tryParseAttributes(String line) {
		if (tryParse(line)) {
			AttributesBuilder result = new AttributesBuilder();
			setItemStats(result);
			return result.toAttributes();
		}
		return null;
	}

	public boolean hasUnusedMatch() {
		return hasMatch() && !matchUsed && pattern.canHaveUsefulMatch();
	}

	public void setMatched(IntConsumer consumer) {
		if (hasMatch()) {
			if (matchUsed) {
				throw new IllegalStateException("Already used: " + matchedLine);
			}
			consumer.accept(getInt());
			this.matchUsed = true;
		}
	}

	public void setStat(AttributesBuilder stats) {
		if (pattern.canHaveUsefulMatch() && hasMatch()) {
			if (matchUsed) {
				throw new IllegalStateException("Already used: " + matchedLine);
			}
			setItemStats(stats);
			this.matchUsed = true;
		}
	}

	public int groupCount() {
		return parsedValues != null ? parsedValues.length : 0;
	}

	public String getAlias() {
		return pattern.getAlias();
	}

	@Override
	public String toString() {
		return matchedLine;
	}
}
