package wow.scraper.parser.tooltip;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.commons.model.effect.Effect;
import wow.scraper.model.RandomEnchant;
import wow.scraper.parser.effect.ItemStatParser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2025-04-09
 */
@RequiredArgsConstructor
public class RandomEnchantParser {
	private static final Pattern RANDOM_ENCHANT_PATTERN = Pattern.compile(
			"<li><div>" +
			"\\s*<span class=\"q\\d\">(.+?)</span>" +
			".*?<br />" +
			"\\s*(.+?)" +
			"\\s*</div></li>", Pattern.MULTILINE
	);

	private static final String RANDOM_ENCHANT_OPENING_DIV = "<div class=\"random-enchantments\"";
	private static final String ANY_OPENING_DIV = "<div";
	private static final String ANY_ENCLOSING_DIV = "</div>";

	private final String html;
	private int pos = 0;

	private final Supplier<ItemStatParser> itemStatParserFactory;

	@Getter
	private final List<RandomEnchant> randomEnchants = new ArrayList<>();

	public void parse() {
		while (pos < html.length()) {
			var position = findDiv(RANDOM_ENCHANT_OPENING_DIV);

			if (position != null) {
				var randomEnchantDiv = position.substring(html);
				parseRandomEnchantDiv(randomEnchantDiv);
			}
		}
	}

	private SubStrPosition findDiv(String openingDiv) {
		int openingPos = html.indexOf(openingDiv, pos);

		if (openingPos < 0) {
			pos = html.length();
			return null;
		}

		pos = openingPos + openingDiv.length();

		while (pos < html.length()) {
			int openingDivPos = html.indexOf(ANY_OPENING_DIV, pos);
			int enclosingDivPos = html.indexOf(ANY_ENCLOSING_DIV, pos);

			if (enclosingDivPos < 0) {
				throw new IllegalArgumentException("No enclosing DIV");
			}

			if (openingDivPos < 0 || enclosingDivPos < openingDivPos) {
				pos = enclosingDivPos + ANY_ENCLOSING_DIV.length();
				return new SubStrPosition(openingPos, pos);
			}

			findDiv(ANY_OPENING_DIV);
		}

		// should never reach this position
		return null;
	}

	private record SubStrPosition(int begin, int end) {
		String substring(String string) {
			return string.substring(begin, end);
		}
	}

	private void parseRandomEnchantDiv(String randomEnchantDiv) {
		var matcher = RANDOM_ENCHANT_PATTERN.matcher(randomEnchantDiv.replace("\n", " "));

		while (matcher.find()) {
			var nameSuffix = parsePrefix(matcher.group(1));
			var effects = parseEffects(matcher.group(2));
			var randomEnchant = new RandomEnchant(nameSuffix, effects);

			randomEnchants.add(randomEnchant);
		}
	}

	private String parsePrefix(String nameSuffix) {
		var prefixToRemove = "...";

		if (nameSuffix.startsWith(prefixToRemove)) {
			return nameSuffix.substring(prefixToRemove.length());
		}

		return nameSuffix;
	}

	private List<Effect> parseEffects(String effectListStr) {
		var itemStatParser = itemStatParserFactory.get();

		Stream.of(effectListStr.split(","))
				.map(String::trim)
				.map(this::replaceWithUpperValue)
				.forEach(itemStatParser::parseItemEffect);

		return itemStatParser.getItemEffects();
	}

	private String replaceWithUpperValue(String string) {
		return string.replaceAll("\\(\\d+ - (\\d+)\\)", "$1");
	}
}
