package wow.scraper.repository.impl.excel.stat;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.scraper.parser.stat.StatPattern;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-13
 */
@AllArgsConstructor
public class StatPatternExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final List<StatPattern> itemStatPatterns;
	private final List<StatPattern> enchantStatPatterns;
	private final List<StatPattern> gemStatPatterns;
	private final List<StatPattern> socketBonusStatPatterns;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new StatPatternSheetParser("item", itemStatPatterns),
				new StatPatternSheetParser("enchant", enchantStatPatterns),
				new StatPatternSheetParser("gem", gemStatPatterns),
				new StatPatternSheetParser("socket", socketBonusStatPatterns)
		);
	}
}
