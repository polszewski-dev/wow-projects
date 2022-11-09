package wow.commons.repository.impl.parsers.gems;

import wow.commons.model.attributes.Attributes;
import wow.commons.repository.impl.parsers.stats.StatParser;
import wow.commons.repository.impl.parsers.stats.StatPatternRepository;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2021-03-24
 */
public final class GemStatsParser {
	public static Attributes tryParseStats(String line) {
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

	private static Attributes tryParseSingleStat(String line) {
		StatParser parser = StatPatternRepository.getInstance().getGemStatParser();
		return parser.tryParseSingleStat(line);
	}

	private GemStatsParser() {}
}
