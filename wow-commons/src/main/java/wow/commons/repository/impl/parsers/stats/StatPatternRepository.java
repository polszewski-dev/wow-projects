package wow.commons.repository.impl.parsers.stats;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
				throw new RuntimeException(e);
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
