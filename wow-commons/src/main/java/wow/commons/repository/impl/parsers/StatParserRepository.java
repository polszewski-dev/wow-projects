package wow.commons.repository.impl.parsers;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-04-04
 */
class StatParserRepository {
	private final List<StatParser> itemStatParsers = new ArrayList<>();
	private final List<StatParser> gemStatParsers = new ArrayList<>();
	private final List<StatParser> socketBonusStatParsers = new ArrayList<>();
	private final Map<String, List<StatParser>> aliases = new HashMap<>();

	private static StatParserRepository instance;

	public static StatParserRepository getInstance() {
		if (instance == null) {
			instance = new StatParserRepository();
			try {
				instance.init();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	private void init() throws IOException, InvalidFormatException {
		var statParserExcelParser = new StatParserExcelParser(
				this.itemStatParsers,
				this.gemStatParsers,
				this.socketBonusStatParsers,
				this.aliases
		);
		statParserExcelParser.readFromXls();
	}

	public List<StatParser> getItemStatParsers() {
		return itemStatParsers;
	}

	public List<StatParser> getGemStatParsers() {
		return gemStatParsers;
	}

	public List<StatParser> getSocketBonusStatParsers() {
		return socketBonusStatParsers;
	}

	public void resetAllItemParsers() {
		for (StatParser pattern : itemStatParsers) {
			pattern.reset();
		}
	}

	public void resetAllGemParsers() {
		for (StatParser pattern : gemStatParsers) {
			pattern.reset();
		}
	}

	public void resetAllSocketBonusParsers() {
		for (StatParser pattern : socketBonusStatParsers) {
			pattern.reset();
		}
	}

	public StatParser getByAlias(String alias) {
		List<StatParser> parsers = aliases.get(alias);
		if (parsers.size() == 1) {
			return parsers.get(0);
		}
		if (parsers.stream().noneMatch(StatParser::hasMatch)) {
			return parsers.get(0);
		}
		List<StatParser> parsersWithMatch = parsers.stream()
												   .filter(StatParser::hasMatch)
												   .collect(Collectors.toList());
		if (parsersWithMatch.size() == 1) {
			return parsersWithMatch.get(0);
		}
		throw new IllegalArgumentException(alias);
	}
}
