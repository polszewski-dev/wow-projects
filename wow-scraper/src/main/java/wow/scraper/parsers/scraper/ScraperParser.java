package wow.scraper.parsers.scraper;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
public abstract class ScraperParser<P extends ScraperPattern<?>, M extends ScraperMatcher<P, ?, N>, N extends ScraperMatcherParams> {
	private final List<M> matchers;
	protected final List<M> successfulMatchers = new ArrayList<>();

	protected ScraperParser(List<P> patterns) {
		this.matchers = patterns.stream()
				.map(this::createMatcher)
				.toList();
	}

	protected abstract M createMatcher(P pattern);

	protected abstract N createMatcherParams(String line);

	public boolean tryParse(String line) {
		N params = createMatcherParams(line);

		for (var matcher : matchers) {
			if (matcher.tryParse(params)) {
				successfulMatchers.add(matcher);
				return true;
			}
		}
		return false;
	}

	public M getUniqueSuccessfulMatcher() {
		if (successfulMatchers.size() != 1) {
			throw new IllegalArgumentException();
		}
		return successfulMatchers.get(0);
	}
}
