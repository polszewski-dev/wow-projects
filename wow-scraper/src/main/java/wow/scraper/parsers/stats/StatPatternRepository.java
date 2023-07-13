package wow.scraper.parsers.stats;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.pve.GameVersionId;

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
	private final List<StatPattern> enchantStatPatterns = new ArrayList<>();
	private final List<StatPattern> gemStatPatterns = new ArrayList<>();
	private final List<StatPattern> socketBonusStatPatterns = new ArrayList<>();

	@Value("${stat.parsers.xls.file.path}")
	private String xlsFilePath;

	@PostConstruct
	public void init() throws IOException {
		var statParserExcelParser = new StatPatternExcelParser(
				xlsFilePath,
				this.itemStatPatterns,
				this.enchantStatPatterns,
				this.gemStatPatterns,
				this.socketBonusStatPatterns
		);
		statParserExcelParser.readFromXls();
		validateAll();
	}

	private void validateAll() {
		assertNoDuplicates(itemStatPatterns);
		assertNoDuplicates(enchantStatPatterns);
		assertNoDuplicates(gemStatPatterns);
		assertNoDuplicates(socketBonusStatPatterns);
	}

	private void assertNoDuplicates(List<StatPattern> patterns) {
		List<String> duplicatePatterns = patterns.stream()
				.map(x -> x.getPattern().pattern() + x.getRequiredVersion().stream().sorted().map(Enum::name).collect(Collectors.joining(",")))
				.collect(Collectors.groupingBy(x -> x, Collectors.counting()))
				.entrySet().stream()
				.filter(x -> x.getValue() > 1)
				.map(Map.Entry::getKey)
				.toList();

		if (!duplicatePatterns.isEmpty()) {
			throw new IllegalArgumentException("Duplicate patterns detected: " + duplicatePatterns);
		}
	}

	public StatParser getItemStatParser(GameVersionId gameVersion) {
		return getStatParser(itemStatPatterns, gameVersion);
	}

	public StatParser getEnchantStatParser(GameVersionId gameVersion) {
		return getStatParser(enchantStatPatterns, gameVersion);
	}

	public StatParser getGemStatParser(GameVersionId gameVersion) {
		return getStatParser(gemStatPatterns, gameVersion);
	}

	public StatParser getSocketBonusStatParser(GameVersionId gameVersion) {
		return getStatParser(socketBonusStatPatterns, gameVersion);
	}

	private StatParser getStatParser(List<StatPattern> patterns, GameVersionId gameVersion) {
		return new StatParser(
				patterns.stream()
						.filter(x -> x.supports(gameVersion))
						.toList()
		);
	}
}
