package wow.commons.repository.impl.parsers.stats;

import wow.commons.model.Duration;

import static wow.commons.util.ParserUtil.parseMultipleInts;

/**
 * User: POlszewski
 * Date: 2022-10-23
 */
public class CooldownParser {
	public static Duration parseCooldown(String value) {
		if (value == null) {
			return null;
		}

		int[] parsedValues;

		parsedValues = parseMultipleInts("(\\d+) Mins?", value);
		if (parsedValues != null) {
			return Duration.seconds(60 * parsedValues[0]);
		}

		parsedValues = parseMultipleInts("(\\d+) Min,? (\\d+) Secs?", value);
		if (parsedValues != null) {
			return Duration.seconds(60 * parsedValues[0] + parsedValues[1]);
		}

		parsedValues = parseMultipleInts("(\\d+) Hour", value);
		if (parsedValues != null) {
			return Duration.seconds(60 * 60 * parsedValues[0]);
		}

		throw new IllegalArgumentException(value);
	}
}
