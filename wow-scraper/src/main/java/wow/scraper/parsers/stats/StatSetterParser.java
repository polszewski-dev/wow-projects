package wow.scraper.parsers.stats;

import wow.scraper.parsers.setters.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: POlszewski
 * Date: 2023-04-22
 */
class StatSetterParser {
	private String line;
	private int groupNo;

	public StatSetterParser(String line) {
		this.line = line.trim();
		this.groupNo = -1;
	}

	public StatSetter parse() {
		parseGroupNo();
		return getStatSetter();
	}

	private void parseGroupNo() {
		Pattern pattern = Pattern.compile("^(.*):(\\d+)$");
		Matcher matcher = pattern.matcher(line);

		if (!matcher.find()) {
			return;
		}

		this.line = matcher.group(1);
		this.groupNo = Integer.parseInt(matcher.group(2));
	}

	private StatSetter getStatSetter() {
		return switch (line) {
			case "Proc" -> new ProcStatSetter(Math.max(groupNo, 0));
			case "OnUse" -> new OnUseStatSetter(Math.max(groupNo, 0));
			case "Equivalent" -> new EquivalentStatSetter(Math.max(groupNo, 0));
			case "Misc" -> new MiscStatSetter();
			case "Expression" -> new ExpressionStatSetter();
			case "Ignored" -> IgnoreStatSetter.INSTANCE;
			default -> new IntStatSetter(line, Math.max(groupNo, 1));
		};
	}
}