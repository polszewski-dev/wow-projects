package wow.commons.repository.impl.parser.spell;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.spell.BuffRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.spell.SpellBaseExcelSheetNames.BUFFS;

/**
 * User: POlszewski
 * Date: 2021-09-25
 */
@AllArgsConstructor
public class BuffExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final BuffRepositoryImpl buffRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new BuffSheetParser(BUFFS, buffRepository)
		);
	}
}
