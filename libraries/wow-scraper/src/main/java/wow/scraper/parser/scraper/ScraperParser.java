package wow.scraper.parser.scraper;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
public abstract class ScraperParser<P extends ScraperPattern<?>, M extends ScraperMatcher<P, ?, N>, N extends ScraperMatcherParams> {
	private final List<M> matchers;
	private M successfulMatcher;

	protected ScraperParser(List<P> patterns) {
		this.matchers = patterns.stream()
				.map(this::createMatcher)
				.toList();
	}

	protected abstract M createMatcher(P pattern);

	protected abstract N createMatcherParams(String line);

	public boolean tryParse(String line) {
		if (successfulMatcher != null) {
			throw new IllegalStateException();
		}

		N params = createMatcherParams(line);

		for (var matcher : matchers) {
			if (matcher.tryParse(params)) {
				successfulMatcher = matcher;
				return true;
			}
		}
		return false;
	}

	public Optional<M> getSuccessfulMatcher() {
		return Optional.ofNullable(successfulMatcher);
	}
}
