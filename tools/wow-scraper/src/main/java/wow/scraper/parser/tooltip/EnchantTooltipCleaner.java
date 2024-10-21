package wow.scraper.parser.tooltip;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-10-07
 */
@RequiredArgsConstructor
@Getter
public class EnchantTooltipCleaner {
	private final String line;
	private String cleanedTooltip;
	private Integer requiredItemLevel = null;

	private static final List<Pattern> PATTERNS = Stream.of(
			"Requires a level (\\d+) or higher item\\.",
			"Can only be attached to level (\\d+) and higher items\\.",
			"Only usable on items level (\\d+) and above\\.",
			"Only useable on items level (\\d+) and above\\."
			)
			.map(Pattern::compile)
			.toList();

	public String cleanEnchantTooltip() {
		cleanedTooltip = line.replace("Does not stack with other enchantments for the selected equipment slot.", "");

		for (var pattern : PATTERNS) {
			var matcher = pattern.matcher(cleanedTooltip);
			if (matcher.find()) {
				cleanedTooltip = cleanedTooltip.replace(matcher.group(), "");
				requiredItemLevel = Integer.valueOf(matcher.group(1));
			}
		}

		cleanedTooltip = cleanedTooltip.trim();

		return cleanedTooltip;
	}
}
