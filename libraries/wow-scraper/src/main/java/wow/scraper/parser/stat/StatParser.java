package wow.scraper.parser.stat;

import wow.commons.model.attribute.Attributes;
import wow.scraper.parser.scraper.ScraperParser;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-23
 */
public class StatParser extends ScraperParser<StatPattern, StatMatcher, StatMatcherParams> {
	public StatParser(List<StatPattern> patterns) {
		super(patterns);
	}

	@Override
	protected StatMatcher createMatcher(StatPattern pattern) {
		return new StatMatcher(pattern);
	}

	@Override
	protected StatMatcherParams createMatcherParams(String line) {
		return new StatMatcherParams(line);
	}

	public Attributes getParsedStats() {
		return getSuccessfulMatcher()
				.map(StatMatcher::getAttributes)
				.orElse(Attributes.EMPTY);
	}
}
