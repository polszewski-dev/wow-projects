package wow.scraper.parsers;

import lombok.Getter;
import wow.commons.model.Money;
import wow.commons.model.pve.Phase;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
public abstract class AbstractTooltipParser {
	protected final String htmlTooltip;

	protected List<String> lines;
	protected int currentLineIdx;

	protected final Integer itemId;
	protected String name;

	public static final boolean ERROR_ON_UNMATCHED_LINE = true;
	public static final Set<String> UNMATCHED_LINES = new TreeSet<>();

	protected AbstractTooltipParser(Integer itemId, String htmlTooltip) {
		this.itemId = itemId;
		this.htmlTooltip = htmlTooltip;
	}

	public final void parse() {
		this.lines = cleanTooltip(htmlTooltip);

		beforeParse();

		this.name = lines.get(0);

		for (currentLineIdx = 1; currentLineIdx < lines.size(); ++currentLineIdx) {
			String currentLine = lines.get(currentLineIdx);
			parseLine(currentLine);
		}

		afterParse();
	}

	protected abstract void parseLine(String currentLine);
	protected abstract void beforeParse();
	protected abstract void afterParse();

	protected Phase parsePhase(String line) {
		String sub = line.substring("Phase ".length());
		return Phase.parse("TBC_P" + sub);
	}

	protected int parseItemLevel(String line) {
		String sub = line.substring("Item Level ".length());
		return Integer.parseInt(sub);
	}

	protected Money parseSellPrice(String line) {
		String sub = line.substring("Sell Price: ".length());
		return Money.parse(sub.replace(" ", ""));
	}

	protected void unmatchedLine(String line) {
		if (ERROR_ON_UNMATCHED_LINE) {
			throw new IllegalArgumentException(line);
		}

		line = line
				.replace("(", "\\(")
				.replace(")", "\\)")
				.replace("+", "\\+")
				.replaceAll("(\\d+.\\d+)", "\\(\\\\d+\\.\\\\d+\\)")
				.replaceAll("(\\d+)", "\\(\\\\d+\\)")
				.replace("[", "\\[")
				.replace("]", "\\]")
		;

		UNMATCHED_LINES.add(line);
	}

	protected static List<String> cleanTooltip(String tooltip) {
		tooltip = tooltip
				.replaceAll("<!--.*?-->", "")
				.replaceAll("<a[^>]*?>", "")
				.replace("</a>", "")
				.replaceAll("<div[^>]*?>", "\n")
				.replace("<br>", "\n")
				.replace("<br />", "\n")
				.replace("<td>", "\n")

				.replaceAll("<span class=\"moneygold\">(\\d+)</span>", "$1g")
				.replaceAll("<span class=\"moneysilver\">(\\d+)</span>", "$1s")
				.replaceAll("<span class=\"moneycopper\">(\\d+)</span>", "$1c")

				.replace("<dfn title=\"her\">his</dfn>", "his")

				.replaceAll("</[^>]+?>", "\n")
				.replaceAll("<[^>]+?>", "")
		;

		if (tooltip.contains("<") || tooltip.contains(">")) {
			throw new IllegalArgumentException("Still something left");
		}

		tooltip = tooltip
				.replace("&nbsp;", " ")
				.replace("&lt;", "<")
				.replace("&gt;", ">")
				.replace("&quot;", "\"")
		;

		if (tooltip.matches("&[a-z]+;")) {
			throw new IllegalArgumentException("Still something left");
		}

		return Stream.of(tooltip.split("\n"))
				.map(String::trim)
				.filter(x -> !x.isBlank())
				.collect(Collectors.toList());
	}
}
