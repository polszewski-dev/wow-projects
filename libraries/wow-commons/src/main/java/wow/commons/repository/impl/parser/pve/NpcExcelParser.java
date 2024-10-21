package wow.commons.repository.impl.parser.pve;

import lombok.AllArgsConstructor;
import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.pve.NpcRepositoryImpl;
import wow.commons.repository.pve.ZoneRepository;

import java.io.InputStream;
import java.util.stream.Stream;

import static wow.commons.repository.impl.parser.pve.PveBaseExcelSheetNames.NPCS;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@AllArgsConstructor
public class NpcExcelParser extends ExcelParser {
	private final String xlsFilePath;
	private final NpcRepositoryImpl npcRepository;
	private final ZoneRepository zoneRepository;

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath(xlsFilePath);
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new NpcSheetParser(NPCS, npcRepository, zoneRepository)
		);
	}
}
