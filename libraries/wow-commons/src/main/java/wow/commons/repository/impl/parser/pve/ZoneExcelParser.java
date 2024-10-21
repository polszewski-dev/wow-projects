package wow.commons.repository.impl.parser.pve;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.pve.ZoneRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.pve.PveBaseExcelSheetNames.ZONES;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@AllArgsConstructor
public class ZoneExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final ZoneRepositoryImpl zoneRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new ZoneSheetParser(ZONES, zoneRepository)
		);
	}
}
