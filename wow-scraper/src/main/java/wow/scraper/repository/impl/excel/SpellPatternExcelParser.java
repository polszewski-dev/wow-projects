package wow.scraper.repository.impl.excel;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.scraper.repository.impl.SpellPatternRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-08-29
 */
@AllArgsConstructor
public class SpellPatternExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final SpellPatternRepositoryImpl spellPatternRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new SpellPatternSheetParser("spells", spellPatternRepository)
		);
	}
}
