package wow.scraper.parsers.stats;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-04-04
 */
public class StatPatternRepository {
	private final List<StatPattern> itemStatPatterns = new ArrayList<>();
	private final List<StatPattern> gemStatPatterns = new ArrayList<>();
	private final List<StatPattern> socketBonusStatPatterns = new ArrayList<>();

	private static StatPatternRepository instance;

	public static StatPatternRepository getInstance() {
		if (instance == null) {
			instance = new StatPatternRepository();
			try {
				instance.init();
			} catch (Exception e) {
				throw new IllegalStateException("Initialization failed", e);
			}
		}
		return instance;
	}

	private void init() throws IOException, InvalidFormatException {
		var statParserExcelParser = new StatPatternExcelParser(
				this.itemStatPatterns,
				this.gemStatPatterns,
				this.socketBonusStatPatterns
		);
		statParserExcelParser.readFromXls();
		validateAll();
	}

	private void validateAll() {
		assertNoDuplicates(itemStatPatterns);
		assertNoDuplicates(gemStatPatterns);
		assertNoDuplicates(socketBonusStatPatterns);
	}

	private void assertNoDuplicates(List<StatPattern> patterns) {
		List<String> duplicatePatterns = patterns.stream()
				.map(x -> x.getPattern().pattern())
				.collect(Collectors.groupingBy(x -> x, Collectors.counting()))
				.entrySet().stream()
				.filter(x -> x.getValue() > 1)
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		if (!duplicatePatterns.isEmpty()) {
			throw new IllegalArgumentException("Duplicate patterns detected: " + duplicatePatterns);
		}
	}

	public StatParser getItemStatParser() {
		return new StatParser(itemStatPatterns);
	}

	public StatParser getGemStatParser() {
		return new StatParser(gemStatPatterns);
	}

	public StatParser getSocketBonusStatParser() {
		return new StatParser(socketBonusStatPatterns);
	}
}
