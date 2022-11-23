package wow.commons.repository.impl.parsers.stats;

import wow.commons.util.ExcelParser;
import wow.commons.util.ExcelSheetReader;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-13
 */
public class StatPatternExcelParser extends ExcelParser {
	private final List<StatPattern> itemStatPatterns;
	private final List<StatPattern> gemStatPatterns;
	private final List<StatPattern> socketBonusStatPatterns;

	public StatPatternExcelParser(
			List<StatPattern> itemStatPatterns,
			List<StatPattern> gemStatPatterns,
			List<StatPattern> socketBonusStatPatterns) {
		this.itemStatPatterns = itemStatPatterns;
		this.gemStatPatterns = gemStatPatterns;
		this.socketBonusStatPatterns = socketBonusStatPatterns;
	}

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath("/xls/stat_parsers.xls");
	}

	@Override
	protected Stream<ExcelSheetReader> getSheetReaders() {
		return Stream.of(
				new PatternSheetReader("base", itemStatPatterns, true),
				new PatternSheetReader("onuse", itemStatPatterns, true),
				new PatternSheetReader("proc", itemStatPatterns, true),
				new PatternSheetReader("set", itemStatPatterns, true),
				new PatternSheetReader("ignored", itemStatPatterns, true),
				new PatternSheetReader("misc_bonus", itemStatPatterns, true),
				new PatternSheetReader("misc_onuse", itemStatPatterns, true),
				new PatternSheetReader("misc_proc", itemStatPatterns, true),
				new PatternSheetReader("misc_set", itemStatPatterns, true),
				new PatternSheetReader("gem", gemStatPatterns, false),
				new PatternSheetReader("socket", socketBonusStatPatterns, false)
		);
	}
}
