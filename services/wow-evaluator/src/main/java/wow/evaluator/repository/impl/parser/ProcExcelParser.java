package wow.evaluator.repository.impl.parser;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.evaluator.repository.impl.ProcInfoRepositoryImpl;

import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
public class ProcExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final ProcInfoRepositoryImpl procInfoRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(new ProcSheetParser(Pattern.compile("proc_.+"), procInfoRepository));
	}
}
