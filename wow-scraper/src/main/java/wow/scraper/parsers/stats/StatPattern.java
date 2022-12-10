package wow.scraper.parsers.stats;

import lombok.Getter;
import wow.scraper.parsers.setters.StatSetter;
import wow.scraper.parsers.setters.StatSetterParams;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2022-10-23
 */
@Getter
public class StatPattern {
	private final Pattern pattern;
	private final boolean literal;
	private final List<StatSetter> setters;
	private final StatSetterParams params;

	public StatPattern(String pattern, List<StatSetter> setters, StatSetterParams params) {
		this.pattern = Pattern.compile(generalize(pattern), Pattern.CASE_INSENSITIVE);
		this.literal = pattern.startsWith("\\Q") && pattern.endsWith("\\E");
		this.setters = setters;
		this.params = params;
	}

	public Matcher getMatcher(String line) {
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			return matcher;
		}
		return null;
	}

	private static String generalize(String pattern) {
		pattern = pattern.replace("(\\d+)", "([+-]?\\d+|[+-]?\\d+\\.\\d+)");
		return "^" + pattern + "$";
	}
}
