package wow.minmax.repository.impl.parsers.config;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.minmax.repository.impl.MinmaxConfigRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
@AllArgsConstructor
public class MinMaxConfigExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final MinmaxConfigRepositoryImpl configRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new ViewConfigSheetParser("view", configRepository),
				new FindUpgradesConfigSheetParser("find_upgrades", configRepository)
		);
	}
}
