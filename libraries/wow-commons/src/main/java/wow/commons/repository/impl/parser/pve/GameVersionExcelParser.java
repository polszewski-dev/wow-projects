package wow.commons.repository.impl.parser.pve;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.pve.GameVersionRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
public class GameVersionExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final GameVersionRepositoryImpl gameVersionRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new GameVersionSheetParser("versions", gameVersionRepository)
		);
	}
}
