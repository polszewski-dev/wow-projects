package wow.commons.repository.impl.parsers.stats;

import wow.commons.util.ExcelParser;
import wow.commons.util.ExcelSheetParser;

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
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new PatternSheetParser("base", itemStatPatterns, true),
				new PatternSheetParser("onuse", itemStatPatterns, true),
				new PatternSheetParser("proc", itemStatPatterns, true),
				new PatternSheetParser("set", itemStatPatterns, true),
				new PatternSheetParser("ignored", itemStatPatterns, true),
				new PatternSheetParser("misc_bonus", itemStatPatterns, true),
				new PatternSheetParser("misc_onuse", itemStatPatterns, true),
				new PatternSheetParser("misc_proc", itemStatPatterns, true),
				new PatternSheetParser("misc_set", itemStatPatterns, true),
				new PatternSheetParser("gem", gemStatPatterns, false),
				new PatternSheetParser("socket", socketBonusStatPatterns, false)
		);
	}
}
