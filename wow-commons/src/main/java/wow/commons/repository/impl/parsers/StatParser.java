package wow.commons.repository.impl.parsers;

import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.repository.impl.parsers.setters.ParsedStatSetter;
import wow.commons.repository.impl.parsers.setters.StatSetterParams;
import wow.commons.util.AttributesBuilder;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2021-03-25
 */
public class StatParser {
	private final Pattern pattern;
	private final List<ParsedStatSetter> setters;
	private final StatSetterParams params;
	private String matchedLine;
	private String[] parsedValues;
	private boolean matchUsed;

	public StatParser(String pattern, List<ParsedStatSetter> setters, StatSetterParams params) {
		this.pattern = Pattern.compile("^" + pattern + "$");
		this.setters = setters;
		this.params = params;
	}

	void reset() {
		this.matchedLine = null;
		this.parsedValues = null;
		this.matchUsed = false;
	}

	boolean hasMatch() {
		return parsedValues != null;
	}

	public int get() {
		return get(1);
	}

	public int get(int i) {
		this.matchUsed = true;
		return getParsedIntValue(i);
	}

	public double getDouble() {
		return getDouble(1);
	}

	public double getDouble(int i) {
		this.matchUsed = true;
		return getParsedDoubleValue(i);
	}

	public String getString() {
		return getString(1);
	}

	public String getString(int i) {
		this.matchUsed = true;
		return getParsedValue(i);
	}

	private String getParsedValue(int i) {
		if (!hasMatch()) {
			throw new IllegalArgumentException();
		}
		if (i == 0) {
			return matchedLine;
		}
		return parsedValues[i - 1];
	}

	private int getParsedIntValue(int i) {
		return Integer.parseInt(getParsedValue(i));
	}

	private double getParsedDoubleValue(int i) {
		return Double.parseDouble(getParsedValue(i));
	}

	public StatSetterParams getParams() {
		return params;
	}

	public Integer evalParam(String paramValue) {
		if (paramValue == null) {
			return null;
		}
		if (paramValue.startsWith("$")) {
			int groupNo = Integer.parseInt(paramValue.substring(1));
			return get(groupNo);
		}
		return Integer.valueOf(paramValue);
	}

	public Percent evalParamPercent(String paramValue) {
		Integer value = evalParam(paramValue);
		return value != null ? Percent.of(value) : null;
	}

	public String evalParamString(String paramValue) {
		if (paramValue == null) {
			return null;
		}
		if (paramValue.startsWith("$")) {
			int groupNo = Integer.parseInt(paramValue.substring(1));
			return getString(groupNo);
		}
		return paramValue;
	}

	String getMatchedLine() {
		return matchedLine;
	}

	public boolean tryParse(String line) {
		Matcher matcher = getMatcher(line);
		if (matcher == null) {
			return false;
		}
		if (canHaveUsefulMatch() && matchedLine != null) {
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

	private Matcher getMatcher(String line) {
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			return matcher;
		}
		return null;
	}

	private void setItemStats(AttributesBuilder itemStats) {
		for (ParsedStatSetter setter : setters) {
			setter.setStats(itemStats, this);
		}
	}

	boolean hasUnusedMatch() {
		return hasMatch() && !matchUsed && canHaveUsefulMatch();
	}

	Attributes resetAndTryParse(String line) {
		reset();
		if (tryParse(line)) {
			AttributesBuilder result = new AttributesBuilder();
			setItemStats(result);
			return result.toAttributes();
		}
		return null;
	}

	void setMatched(IntConsumer consumer) {
		if (hasMatch()) {
			if (matchUsed) {
				throw new IllegalStateException("Already used: " + matchedLine);
			}
			consumer.accept(get());
			this.matchUsed = true;
		}
	}

	void setStat(AttributesBuilder stats) {
		if (canHaveUsefulMatch() && hasMatch()) {
			if (matchUsed) {
				throw new IllegalStateException("Already used: " + matchedLine);
			}
			setItemStats(stats);
			this.matchUsed = true;
		}
	}

	private boolean canHaveUsefulMatch() {
		return setters != null && setters.stream().anyMatch(ParsedStatSetter::hasAnySetter);
	}

	@Override
	public String toString() {
		return matchedLine;
	}

	public int groupCount() {
		return parsedValues != null ? parsedValues.length : 0;
	}
}
