package wow.scraper.importer.parser;

import lombok.AllArgsConstructor;
import wow.commons.model.pve.Side;
import wow.scraper.model.WowheadQuestInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@AllArgsConstructor
public class QuestInfoParser {
	private final String html;

	public WowheadQuestInfo parse() {
		var questInfo = new WowheadQuestInfo();
		questInfo.setHtml(html);
		questInfo.setRequiredLevel(parseQuestRequiredLevel());
		questInfo.setRequiredSide(parseRequiredSide());
		return questInfo;
	}

	private Integer parseQuestRequiredLevel() {
		Pattern pattern = Pattern.compile("\\[li]Requires level (\\d+)\\[\\\\/li]");
		Matcher matcher = pattern.matcher(html);
		if (matcher.find()) {
			return Integer.valueOf(matcher.group(1));
		}
		return null;
	}

	private Side parseRequiredSide() {
		Pattern pattern = Pattern.compile("\\[li]Side: \\[span class=icon-.+](Horde|Alliance)\\[\\\\/span]\\[\\\\/li]");
		Matcher matcher = pattern.matcher(html);
		if (matcher.find()) {
			return Side.parse(matcher.group(1));
		}
		return null;
	}
}
