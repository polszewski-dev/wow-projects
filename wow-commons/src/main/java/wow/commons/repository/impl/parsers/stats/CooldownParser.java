package wow.commons.repository.impl.parsers.stats;

import wow.commons.model.Duration;

import static wow.commons.util.parser.ParserUtil.parseMultipleInts;

/**
 * User: POlszewski
 * Date: 2022-10-23
 */
public final class CooldownParser {
	public static Duration parseCooldown(String value) {
		if (value == null) {
			return null;
		}

		int[] parsedValues;

		parsedValues = parseMultipleInts("(\\d+) Mins?", value);
		if (parsedValues.length > 0) {
			return Duration.seconds(60.0 * parsedValues[0]);
		}

		parsedValues = parseMultipleInts("(\\d+) Min,? (\\d+) Secs?", value);
		if (parsedValues.length > 0) {
			return Duration.seconds(60.0 * parsedValues[0] + parsedValues[1]);
		}

		parsedValues = parseMultipleInts("(\\d+) Hour", value);
		if (parsedValues.length > 0) {
			return Duration.seconds(60.0 * 60.0 * parsedValues[0]);
		}

		throw new IllegalArgumentException(value);
	}

	private CooldownParser() {}
}
