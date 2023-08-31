package wow.scraper.parser.stat;

import lombok.Getter;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.parser.scraper.ScraperPattern;
import wow.scraper.parser.setter.StatSetter;

import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2022-10-23
 */
@Getter
public class StatPattern extends ScraperPattern<StatPatternParams> {
	private final boolean literal;
	private final List<StatSetter> setters;
	private final Set<GameVersionId> requiredVersion;

	public StatPattern(String pattern, List<StatSetter> setters, StatPatternParams params, Set<GameVersionId> requiredVersion) {
		super(generalize(pattern), params, requiredVersion);
		this.literal = pattern.startsWith("\\Q") && pattern.endsWith("\\E");
		this.setters = setters;
		this.requiredVersion = requiredVersion;
	}

	private static String generalize(String pattern) {
		pattern = pattern.replace("(\\d+)", "([+-]?\\d+|[+-]?\\d+\\.\\d+)");
		return "^" + pattern + "$";
	}
}
