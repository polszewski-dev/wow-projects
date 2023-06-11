package wow.scraper.importers.parsers;

import lombok.AllArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2023-05-20
 */
@AllArgsConstructor
public class SpellIdParser {
	private final String html;

	public Integer parse() {
		Pattern pattern = Pattern.compile("spell=(\\d+)");
		Matcher matcher = pattern.matcher(html);
		if (matcher.find()) {
			return Integer.valueOf(matcher.group(1));
		}
		return null;
	}
}
