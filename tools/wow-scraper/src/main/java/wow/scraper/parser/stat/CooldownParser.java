package wow.scraper.parser.stat;

import wow.commons.model.Duration;
import wow.commons.util.parser.ParsedMultipleValues;

import static wow.commons.util.parser.ParserUtil.parseMultipleValues;

/**
 * User: POlszewski
 * Date: 2022-10-23
 */
public final class CooldownParser {
	public static Duration parseCooldown(String value) {
		if (value == null) {
			return null;
		}

		ParsedMultipleValues parsedValues;

		parsedValues = parseMultipleValues("(\\d+|\\d+\\.\\d+)(s| Sec| Secs)", value);
		if (!parsedValues.isEmpty()) {
			return Duration.seconds(parsedValues.getDouble(0));
		}

		parsedValues = parseMultipleValues("(\\d+)(m| Min| Mins)", value);
		if (!parsedValues.isEmpty()) {
			return Duration.seconds(60.0 * parsedValues.getDouble(0));
		}

		parsedValues = parseMultipleValues("(\\d+) Min,? (\\d+) Secs?", value);
		if (!parsedValues.isEmpty()) {
			return Duration.seconds(60.0 * parsedValues.getDouble(0) + parsedValues.getDouble(1));
		}

		parsedValues = parseMultipleValues("(\\d+) (Hrs|Hour|Hours)", value);
		if (!parsedValues.isEmpty()) {
			return Duration.seconds(60.0 * 60.0 * parsedValues.getDouble(0));
		}

		parsedValues = parseMultipleValues("(\\d+)ms", value);
		if (!parsedValues.isEmpty()) {
			return Duration.millis(parsedValues.getInteger(0));
		}

		throw new IllegalArgumentException(value);
	}

	private CooldownParser() {}
}
