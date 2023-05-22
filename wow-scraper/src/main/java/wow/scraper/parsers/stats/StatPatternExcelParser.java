package wow.scraper.parsers.stats;

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
	private final List<StatPattern> enchantStatPatterns;
	private final List<StatPattern> gemStatPatterns;
	private final List<StatPattern> socketBonusStatPatterns;
	private final String xlsFilePath;

	public StatPatternExcelParser(
			String xlsFilePath,
			List<StatPattern> itemStatPatterns,
			List<StatPattern> enchantStatPatterns,
			List<StatPattern> gemStatPatterns,
			List<StatPattern> socketBonusStatPatterns) {
		this.xlsFilePath = xlsFilePath;
		this.itemStatPatterns = itemStatPatterns;
		this.enchantStatPatterns = enchantStatPatterns;
		this.gemStatPatterns = gemStatPatterns;
		this.socketBonusStatPatterns = socketBonusStatPatterns;
	}

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new PatternSheetParser("base", itemStatPatterns),
				new PatternSheetParser("onuse", itemStatPatterns),
				new PatternSheetParser("proc", itemStatPatterns),
				new PatternSheetParser("set", itemStatPatterns),
				new PatternSheetParser("misc_bonus", itemStatPatterns),
				new PatternSheetParser("enchant", enchantStatPatterns),
				new PatternSheetParser("gem", gemStatPatterns),
				new PatternSheetParser("socket", socketBonusStatPatterns)
		);
	}
}
