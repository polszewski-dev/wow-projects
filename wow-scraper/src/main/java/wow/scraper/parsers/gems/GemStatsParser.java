package wow.scraper.parsers.gems;

import lombok.AllArgsConstructor;
import wow.commons.model.attributes.Attributes;
import wow.commons.util.AttributesBuilder;
import wow.scraper.parsers.stats.StatParser;
import wow.scraper.parsers.stats.StatPatternRepository;

/**
 * User: POlszewski
 * Date: 2021-03-24
 */
@AllArgsConstructor
public final class GemStatsParser {
	private StatPatternRepository statPatternRepository;

	public Attributes tryParseStats(String line) {
		AttributesBuilder builder = new AttributesBuilder();

		for (String part : getParts(line)) {
			Attributes singleStat = tryParseSingleStat(part);
			if (singleStat == null) {
				return null;
			}
			builder.addAttributes(singleStat);
		}

		return builder.toAttributes();
	}

	private static String[] getParts(String line) {
		if (line.contains(" & ")) {
			return line.split(" & ");
		} else if (line.contains(", ")) {
			return line.split(", ");
		} else if (line.contains(" and ")) {
			return line.split(" and ");
		} else {
			return new String[] {line};
		}
	}

	private Attributes tryParseSingleStat(String line) {
		StatParser parser = statPatternRepository.getGemStatParser();
		return parser.tryParseSingleStat(line);
	}
}
