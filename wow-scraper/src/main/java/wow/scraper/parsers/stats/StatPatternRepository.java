package wow.scraper.parsers.stats;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-04-04
 */
@Repository
public class StatPatternRepository {
	private final List<StatPattern> itemStatPatterns = new ArrayList<>();
	private final List<StatPattern> gemStatPatterns = new ArrayList<>();
	private final List<StatPattern> socketBonusStatPatterns = new ArrayList<>();

	@Value("${stat.parsers.xls.file.path}")
	private String xlsFilePath;

	@PostConstruct
	public void init() throws IOException, InvalidFormatException {
		var statParserExcelParser = new StatPatternExcelParser(
				xlsFilePath,
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
				.toList();

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
