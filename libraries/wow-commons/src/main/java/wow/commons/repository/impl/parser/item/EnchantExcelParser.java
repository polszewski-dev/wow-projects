package wow.commons.repository.impl.parser.item;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.item.EnchantRepositoryImpl;
import wow.commons.repository.spell.SpellRepository;

import java.io.InputStream;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.item.ItemBaseExcelSheetNames.ENCHANT;

/**
 * User: POlszewski
 * Date: 2022-10-30
 */
@AllArgsConstructor
public class EnchantExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final SourceParserFactory sourceParserFactory;
	private final EnchantRepositoryImpl enchantRepository;
	private final SpellRepository spellRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new EnchantSheetParser(ENCHANT, sourceParserFactory, spellRepository, enchantRepository)
		);
	}
}
