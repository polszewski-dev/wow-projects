package wow.commons.repository.impl.parsers.stats;

import wow.commons.repository.impl.parsers.setters.StatSetter;
import wow.commons.repository.impl.parsers.setters.StatSetterParams;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2022-10-23
 */
public class StatPattern {
	private final Pattern pattern;
	private final List<StatSetter> setters;
	private final StatSetterParams params;
	private final String alias;

	public StatPattern(String pattern, List<StatSetter> setters, StatSetterParams params, String alias) {
		this.pattern = Pattern.compile("^" + pattern + "$");
		this.setters = setters;
		this.params = params;
		this.alias = alias;
	}

	public Pattern getPattern() {
		return pattern;
	}

	public List<StatSetter> getSetters() {
		return setters;
	}

	public StatSetterParams getParams() {
		return params;
	}

	public String getAlias() {
		return alias;
	}

	public Matcher getMatcher(String line) {
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			return matcher;
		}
		return null;
	}

	public boolean canHaveUsefulMatch() {
		return setters != null && setters.stream().anyMatch(statSetter -> !statSetter.isEmpty());
	}
}