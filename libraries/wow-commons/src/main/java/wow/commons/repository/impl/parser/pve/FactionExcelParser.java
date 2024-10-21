package wow.commons.repository.impl.parser.pve;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.pve.FactionRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.pve.PveBaseExcelSheetNames.FACTIONS;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@AllArgsConstructor
public class FactionExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final FactionRepositoryImpl factionRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new FactionSheetParser(FACTIONS, factionRepository)
		);
	}
}
