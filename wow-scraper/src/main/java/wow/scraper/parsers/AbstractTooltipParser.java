package wow.scraper.parsers;

import lombok.Getter;
import org.slf4j.Logger;
import wow.commons.model.Money;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.Phase;
import wow.commons.util.ParserUtil;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
public abstract class AbstractTooltipParser {
	protected final GameVersion gameVersion;

	protected final String htmlTooltip;

	protected List<String> lines;
	protected int currentLineIdx;

	protected final Integer itemId;
	protected String name;

	private static final boolean ERROR_ON_UNMATCHED_LINE = true;
	private static final Set<String> UNMATCHED_LINES = new TreeSet<>();

	protected AbstractTooltipParser(Integer itemId, String htmlTooltip, GameVersion gameVersion) {
		this.gameVersion = gameVersion;
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

	private void parseLine(String currentLine) {
		for (Rule rule : getRules()) {
			if (rule.matchAndTakeAction(currentLine)) {
				return;
			}
		}
		unmatchedLine(currentLine);
	}

	protected abstract Rule[] getRules();

	protected interface Rule {
		boolean matchAndTakeAction(String line);

		static Rule exact(String exactValue, Runnable action) {
			return line -> {
				if (line.equals(exactValue)) {
					action.run();
					return true;
				}
				return false;
			};
		}

		static Rule prefix(String prefix, Consumer<String> action) {
			return line -> {
				String withoutPrefix = ParserUtil.removePrefix(prefix, line);
				if (withoutPrefix != null) {
					action.accept(withoutPrefix);
					return true;
				}
				return false;
			};
		}

		static <T> Rule tryParse(Function<String, T> lineParser, Consumer<T> parsedValueConsumer) {
			return line -> {
				T parsedValue = lineParser.apply(line);
				if (parsedValue != null) {
					parsedValueConsumer.accept(parsedValue);
					return true;
				}
				return false;
			};
		}

		static Rule test(Predicate<String> predicate, Consumer<String> consumer) {
			return line -> {
				if (predicate.test(line)) {
					consumer.accept(line);
					return true;
				}
				return false;
			};
		}

		static <T> Rule testNotNull(Function<String, T> lineParser, Consumer<String> consumer) {
			return test(line -> lineParser.apply(line) != null, consumer);
		}

		static Rule regex(String regex, Consumer<Object[]> matchedValueConsumer) {
			return line -> {
				Object[] matchedValues = ParserUtil.parseMultipleValues(regex, line);
				if (matchedValues != null) {
					matchedValueConsumer.accept(matchedValues);
					return true;
				}
				return false;
			};
		}
	}

	protected abstract void beforeParse();
	protected abstract void afterParse();

	protected Phase parsePhase(String value) {
		return Phase.parse(gameVersion + "_P" + value);
	}

	protected int parseItemLevel(String value) {
		return Integer.parseInt(value);
	}

	protected Money parseSellPrice(String value) {
		return Money.parse(value.replace(" ", ""));
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

	public static void reportUnmatchedLines(Logger log) {
		UNMATCHED_LINES.forEach(log::info);
	}
}
