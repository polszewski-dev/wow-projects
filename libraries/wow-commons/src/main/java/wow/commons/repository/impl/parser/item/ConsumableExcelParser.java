package wow.commons.repository.impl.parser.item;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.item.ConsumableRepositoryImpl;
import wow.commons.repository.spell.SpellRepository;

import java.io.InputStream;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.CONSUMABLE;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@AllArgsConstructor
public class ConsumableExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final SourceParserFactory sourceParserFactory;
	private final SpellRepository spellRepository;
	private final ConsumableRepositoryImpl consumableRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new ConsumableSheetParser(CONSUMABLE, sourceParserFactory, spellRepository, consumableRepository)
		);
	}
}
