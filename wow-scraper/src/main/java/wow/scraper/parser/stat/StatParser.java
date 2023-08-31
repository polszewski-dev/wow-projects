package wow.scraper.parser.stat;

import wow.commons.model.attribute.Attributes;
import wow.commons.util.AttributesBuilder;
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
		return StatMatcherParams.of(line);
	}

	public Attributes tryParseSingleStat(String line) {
		if (tryParse(line)) {
			return getParsedStats();
		}
		return null;
	}

	public Attributes getParsedStats() {
		AttributesBuilder stats = new AttributesBuilder();
		for (StatMatcher matcher : successfulMatchers) {
			matcher.setStat(stats);
		}
		return stats.toAttributes();
	}
}
