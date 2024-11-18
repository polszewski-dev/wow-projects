package wow.commons.repository.impl.parser.character;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.pve.GameVersionRepository;
import wow.commons.repository.spell.SpellRepository;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
public class RaceExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final GameVersionRepository gameVersionRepository;
	private final SpellRepository spellRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new RaceSheetParser("races", gameVersionRepository, spellRepository)
		);
	}
}
