package wow.commons.repository.impl.parsers;

import wow.commons.model.attributes.Attributes;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2021-03-24
 */
public class GemStatsParser {
	public static Attributes tryParseStats(String line) {
		AttributesBuilder builder = new AttributesBuilder();
		String[] parts;

		if (line.contains(" & ")) {
			parts = line.split(" & ");
		} else if (line.contains(", ")) {
			parts = line.split(", ");
		} else if (line.contains(" and ")) {
			parts = line.split(" and ");
		} else {
			parts = new String[] { line };
		}

		for (String part : parts) {
			Attributes singleStat = tryParseSingleStat(part);
			if (singleStat == null) {
				return null;
			}
			builder.addAttributes(singleStat);
		}
		return builder.toAttributes();
	}

	private static Attributes tryParseSingleStat(String line) {
		for (StatParser parser : StatParserRepository.getInstance().getGemStatParsers()) {
			Attributes gemStat = parser.resetAndTryParse(line);
			if (gemStat != null) {
				return gemStat;
			}
		}
		return null;
	}
}
