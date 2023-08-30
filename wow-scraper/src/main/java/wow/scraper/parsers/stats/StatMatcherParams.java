package wow.scraper.parsers.stats;

import lombok.Builder;
import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.scraper.parsers.scraper.ScraperMatcherParams;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2022-12-10
 */
@Getter
@Builder
public class StatMatcherParams implements ScraperMatcherParams {
	private final String originalLine;
	private final String line;
	private final Duration parsedCooldown;
	private final Percent parsedProcChance;
	private final Duration parsedProcCooldown;

	public static StatMatcherParams of(String line) {
		StatMatcherParamsBuilder builder = builder().originalLine(line);

		line = removeNoise(line);
		line = parseProcAndCooldown(line, builder);
		line = removeNoise(line);

		builder.line(line);

		return builder.build();
	}

	private static String removeNoise(String line) {
		line = line.replace("(750ms cooldown)", "").trim();
		line = removePrefixes(line).trim();
		line = removeSuffixes(line).trim();
		return line;
	}

	private static String removePrefixes(String line) {
		String[] prefixes = {
				"Equip: "
		};

		for (String prefix : prefixes) {
			if (line.startsWith(prefix)) {
				line = line.substring(prefix.length());
			}
		}

		return line;
	}

	private static String removeSuffixes(String line) {
		while (line.endsWith(".")) {
			line = line.substring(0, line.length() - ".".length());
		}
		return line;
	}

	private static String parseProcAndCooldown(String line, StatMatcherParamsBuilder builder) {
		line = parseProcChanceAndCooldown(line, builder);
		line = parseProcChance(line, builder);
		line = parseCooldown(line, builder);
		return line;
	}

	private static String parseProcChance(String line, StatMatcherParamsBuilder builder) {
		return parse(line, "\\(Proc chance: (\\d+)%\\)", matcher -> builder.parsedProcChance(Percent.parse(matcher.group(1))));
	}

	private static String parseProcChanceAndCooldown(String line, StatMatcherParamsBuilder builder) {
		return parse(line, "\\(Proc chance: (\\d+)%, (.+?) cooldown\\)", matcher -> {
			builder.parsedProcChance(Percent.parse(matcher.group(1)));
			builder.parsedProcCooldown(CooldownParser.parseCooldown(matcher.group(2)));
		});
	}

	private static String parseCooldown(String line, StatMatcherParamsBuilder builder) {
		return parse(line, " \\((.+?) Cooldown\\)", matcher -> builder.parsedCooldown(CooldownParser.parseCooldown(matcher.group(1))));
	}

	private static String parse(String line, String pattern, Consumer<Matcher> onMatch) {
		Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(line);

		if (!matcher.find()) {
			return line;
		}

		onMatch.accept(matcher);

		return line.replace(matcher.group(), "").trim();
	}
}
