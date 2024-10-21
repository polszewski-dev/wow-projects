package wow.scraper.parser.scraper;

import lombok.Getter;
import wow.commons.model.pve.GameVersionId;
import wow.scraper.util.CommonAssertions;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
@Getter
public abstract class ScraperPattern<P extends ScraperPatternParams> {
	private final Pattern pattern;
	private final String patternString;
	private final P params;
	private final Set<GameVersionId> requiredVersion;

	protected ScraperPattern(String pattern, P params, Set<GameVersionId> requiredVersion) {
		this.pattern = Pattern.compile(generalize(pattern), Pattern.CASE_INSENSITIVE);
		this.patternString = pattern;
		this.params = params;
		this.requiredVersion = requiredVersion;
	}

	public Matcher getMatcher(String line) {
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()) {
			return matcher;
		}
		return null;
	}

	public boolean supports(GameVersionId gameVersion) {
		return requiredVersion.isEmpty() || requiredVersion.contains(gameVersion);
	}

	private static String generalize(String pattern) {
		pattern = pattern.replace("(\\d+)", "([+-]?\\d+|[+-]?\\d+\\.\\d+)");
		return "^" + pattern + "$";
	}

	public String getPatternString() {
		return patternString;
	}

	public String requiredVersionString() {
		return getRequiredVersion().stream()
				.sorted()
				.map(Enum::name)
				.collect(Collectors.joining(","));
	}

	public boolean matches(String pattern, Set<GameVersionId> requiredVersion) {
		return this.patternString.equals(pattern) && this.requiredVersion.equals(requiredVersion);
	}

	public String uniqueKey() {
		return getPatternString() + requiredVersionString();
	}

	@Override
	public String toString() {
		return patternString;
	}

	public static <T extends ScraperPattern<?>> void assertNoDuplicates(List<T> patterns) {
		CommonAssertions.assertNoDuplicates(patterns, ScraperPattern::uniqueKey);
	}
}
