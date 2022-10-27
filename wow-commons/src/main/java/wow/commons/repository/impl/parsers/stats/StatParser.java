package wow.commons.repository.impl.parsers.stats;

import wow.commons.model.attributes.Attributes;
import wow.commons.util.AttributesBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-10-23
 */
public class StatParser {
	private final List<StatMatcher> matchers;

	public StatParser(List<StatPattern> patterns) {
		this.matchers = patterns.stream()
				.map(StatMatcher::new)
				.collect(Collectors.toList());
	}

	public Attributes tryParseSingleStat(String line) {
		for (StatMatcher matcher : matchers) {
			Attributes stat = matcher.tryParseAttributes(line);
			if (stat != null) {
				return stat;
			}
		}
		return null;
	}

	public boolean tryParse(String line) {
		for (StatMatcher matcher : matchers) {
			if (matcher.tryParse(line)) {
				return true;
			}
		}
		return false;
	}

	public Attributes getParsedStats() {
		AttributesBuilder stats = new AttributesBuilder();
		for (StatMatcher matcher : matchers) {
			matcher.setStat(stats);
		}
		return stats.toAttributes();
	}
}
