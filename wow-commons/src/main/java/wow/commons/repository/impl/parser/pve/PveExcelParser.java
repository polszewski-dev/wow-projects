package wow.commons.repository.impl.parser.pve;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.PveRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.pve.PveBaseExcelSheetNames.*;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@AllArgsConstructor
public class PveExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final PveRepositoryImpl pveRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new ZoneSheetParser(ZONES, pveRepository),
				new NpcSheetParser(NPCS, pveRepository),
				new FactionSheetParser(FACTIONS, pveRepository)
		);
	}
}
