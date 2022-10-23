package wow.commons.repository.impl.parsers.stats;

import wow.commons.model.attributes.Attributes;
import wow.commons.util.AttributesBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-10-23
 */
public class StatParser {
	private final List<StatMatcher> matchers;

	private final Map<String, List<StatMatcher>> aliases;

	public StatParser(List<StatPattern> patterns) {
		this.matchers = patterns.stream()
				.map(StatMatcher::new)
				.collect(Collectors.toList());

		this.aliases = this.matchers.stream()
				.filter(x -> x.getAlias() != null)
				.collect(Collectors.groupingBy(StatMatcher::getAlias));
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

	public void checkForNotUsedMatches() {
		for (StatMatcher matcher : matchers) {
			if (matcher.hasUnusedMatch()) {
				throw new IllegalArgumentException("Not used: " + matcher.getMatchedLine());
			}
		}
	}

	public Attributes getParsedStats() {
		AttributesBuilder stats = new AttributesBuilder();
		for (StatMatcher matcher : matchers) {
			matcher.setStat(stats);
		}
		return stats.toAttributes();
	}

	public StatMatcher getByAlias(String alias) {
		List<StatMatcher> matchers = aliases.get(alias);
		if (matchers.size() == 1) {
			return matchers.get(0);
		}
		if (matchers.stream().noneMatch(StatMatcher::hasMatch)) {
			return matchers.get(0);
		}
		List<StatMatcher> matchersWithMatch = matchers.stream()
				.filter(StatMatcher::hasMatch)
				.collect(Collectors.toList());
		if (matchersWithMatch.size() == 1) {
			return matchersWithMatch.get(0);
		}
		throw new IllegalArgumentException(alias);
	}
}
