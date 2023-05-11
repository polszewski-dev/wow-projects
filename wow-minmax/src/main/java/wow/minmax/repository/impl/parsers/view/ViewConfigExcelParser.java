package wow.minmax.repository.impl.parsers.view;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.minmax.repository.impl.ViewConfigRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
@AllArgsConstructor
public class ViewConfigExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final ViewConfigRepositoryImpl viewConfigRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(new ViewConfigSheetParser("view", viewConfigRepository));
	}
}
