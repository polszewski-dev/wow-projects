package wow.character.repository.impl.parser.character;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.character.repository.impl.BaseStatInfoRepositoryImpl;
import wow.commons.repository.pve.GameVersionRepository;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
public class BaseStatInfoExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final GameVersionRepository gameVersionRepository;
	private final BaseStatInfoRepositoryImpl baseStatInfoRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new BaseStatInfoSheetParser("base_stats", gameVersionRepository, baseStatInfoRepository)
		);
	}
}
