package wow.scraper.importer.parser;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static wow.scraper.importer.parser.ZoneNpcListParser.ParseResultType.NPC;
import static wow.scraper.importer.parser.ZoneNpcListParser.ParseResultType.OBJECT;

/**
 * User: POlszewski
 * Date: 2023-06-25
 */
@AllArgsConstructor
public class ZoneNpcListParser {
	private final String html;
	private final List<ParseResult> result = new ArrayList<>();

	public enum ParseResultType {
		NPC, OBJECT
	}

	public record ParseResult(
			ParseResultType type,
			int id
	) {}

	public List<ParseResult> parse() {
		Pattern pattern = Pattern.compile("\\[minibox](.*?)\\[\\\\/minibox]");
		Matcher matcher = pattern.matcher(html);

		while (matcher.find()) {
			parseNpcIds(matcher.group(1));
		}

		return result;
	}

	private void parseNpcIds(String group) {
		Pattern pattern = Pattern.compile("\\[(url=\\?|url=\\\\/\\?|)(npc|object)=(\\d+)]");
		Matcher matcher = pattern.matcher(group);

		while (matcher.find()) {
			ParseResultType type = getType(matcher.group(2));
			int id = Integer.parseInt(matcher.group(3));
			result.add(new ParseResult(type, id));
		}
	}

	private ParseResultType getType(String value) {
		return switch (value) {
			case "npc" -> NPC;
			case "object" -> OBJECT;
			default -> throw new IllegalArgumentException(value);
		};
	}
}
