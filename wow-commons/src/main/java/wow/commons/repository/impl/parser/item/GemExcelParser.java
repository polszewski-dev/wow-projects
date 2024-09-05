package wow.commons.repository.impl.parser.item;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.SpellRepository;
import wow.commons.repository.impl.item.GemRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.GEM;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@AllArgsConstructor
public class GemExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final SourceParserFactory sourceParserFactory;
	private final GemRepositoryImpl gemRepository;
	private final SpellRepository spellRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new GemSheetParser(GEM, sourceParserFactory, spellRepository, gemRepository)
		);
	}
}
