package wow.scraper.parser.scraper;

import lombok.Getter;
import wow.commons.model.pve.GameVersionId;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
@Getter
public abstract class ScraperPattern<P extends ScraperPatternParams> {
	private final Pattern pattern;
	private final P params;
	private final Set<GameVersionId> requiredVersion;

	protected ScraperPattern(String pattern, P params, Set<GameVersionId> requiredVersion) {
		this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
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
}
