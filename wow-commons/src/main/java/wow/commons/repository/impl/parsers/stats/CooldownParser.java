package wow.commons.repository.impl.parsers.stats;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2022-10-23
 */
public class CooldownParser {
	public static Integer parseCooldown(String value) {
		int[] parsedValues;

		parsedValues = tryParseCooldown("^(\\d+) Mins$", value);
		if (parsedValues != null) {
			return 60 * parsedValues[0];
		}

		parsedValues = tryParseCooldown("^(\\d+) Min (\\d+) Secs", value);
		if (parsedValues != null) {
			return 60 * parsedValues[0] + parsedValues[1];
		}

		throw new IllegalArgumentException(value);
	}

	private static int[] tryParseCooldown(String regex, String value) {
		Pattern pattern = Pattern.compile("^" + regex + "$");
		Matcher matcher = pattern.matcher(value);
		if (matcher.find()) {
			int[] result = new int[matcher.groupCount()];
			for (int i = 1; i <= matcher.groupCount(); ++i) {
				result[i - 1] = Integer.parseInt(matcher.group(i));
			}
			return result;
		}
		return null;
	}
}
