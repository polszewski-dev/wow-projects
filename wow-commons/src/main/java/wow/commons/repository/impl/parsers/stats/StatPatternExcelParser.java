package wow.commons.repository.impl.parsers.stats;

import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;

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
				new PatternSheetParser("base", itemStatPatterns),
				new PatternSheetParser("onuse", itemStatPatterns),
				new PatternSheetParser("proc", itemStatPatterns),
				new PatternSheetParser("set", itemStatPatterns),
				new PatternSheetParser("ignored", itemStatPatterns),
				new PatternSheetParser("misc_bonus", itemStatPatterns),
				new PatternSheetParser("misc_onuse", itemStatPatterns),
				new PatternSheetParser("misc_proc", itemStatPatterns),
				new PatternSheetParser("misc_set", itemStatPatterns),
				new PatternSheetParser("gem", gemStatPatterns),
				new PatternSheetParser("socket", socketBonusStatPatterns)
		);
	}
}
