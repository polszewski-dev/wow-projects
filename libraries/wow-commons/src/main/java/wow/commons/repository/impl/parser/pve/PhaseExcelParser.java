package wow.commons.repository.impl.parser.pve;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.pve.PhaseRepositoryImpl;
import wow.commons.repository.pve.GameVersionRepository;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
public class PhaseExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final GameVersionRepository gameVersionRepository;
	private final PhaseRepositoryImpl phaseRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new PhaseSheetParser("phases", gameVersionRepository, phaseRepository)
		);
	}
}
