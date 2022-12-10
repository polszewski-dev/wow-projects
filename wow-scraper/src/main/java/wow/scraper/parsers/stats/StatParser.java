package wow.scraper.parsers.stats;

import wow.commons.model.attributes.Attributes;
import wow.commons.util.AttributesBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-10-23
 */
public class StatParser {
	private final List<StatMatcher> matchers;
	private final List<StatMatcher> successfulMatchers = new ArrayList<>();

	public StatParser(List<StatPattern> patterns) {
		this.matchers = patterns.stream()
				.map(StatMatcher::new)
				.collect(Collectors.toList());
	}

	public boolean tryParse(String line) {
		StatMatcherParams params = StatMatcherParams.of(line);

		for (StatMatcher matcher : matchers) {
			if (matcher.tryParse(params)) {
				successfulMatchers.add(matcher);
				return true;
			}
		}
		return false;
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
