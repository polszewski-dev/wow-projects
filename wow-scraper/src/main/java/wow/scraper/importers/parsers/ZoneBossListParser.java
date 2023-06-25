package wow.scraper.importers.parsers;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2023-06-25
 */
@AllArgsConstructor
public class ZoneBossListParser {
	private final String html;
	private final List<Integer> result = new ArrayList<>();

	public List<Integer> parse() {
		Pattern pattern = Pattern.compile("\\[minibox](.*?)\\[\\\\/minibox]");
		Matcher matcher = pattern.matcher(html);

		while (matcher.find()) {
			parseBossIds(matcher.group(1));
		}

		return result;
	}

	private void parseBossIds(String group) {
		Pattern pattern = Pattern.compile("\\[(url=\\?|url=\\\\/\\?|)npc=(\\d+)]");
		Matcher matcher = pattern.matcher(group);

		while (matcher.find()) {
			result.add(Integer.valueOf(matcher.group(2)));
		}
	}
}
