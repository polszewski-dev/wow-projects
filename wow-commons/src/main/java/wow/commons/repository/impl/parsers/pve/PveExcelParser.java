package wow.commons.repository.impl.parsers.pve;

import polszewski.excel.reader.templates.ExcelParser;
import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.repository.impl.PveRepositoryImpl;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public class PveExcelParser extends ExcelParser {
	private final PveRepositoryImpl pveRepository;

	public PveExcelParser(PveRepositoryImpl pveRepository) {
		this.pveRepository = pveRepository;
	}

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath("/xls/pve_data.xls");
	}

	@Override
	protected Stream<ExcelSheetParser> getSheetParsers() {
		return Stream.of(
				new ZoneSheetParser("zones", pveRepository),
				new BossSheetParser("bosses", pveRepository),
				new FactionSheetParser("factions", pveRepository),
				new BaseStatsSheetParser("base_stats", pveRepository),
				new CombatRatingsSheetParser("combat_ratings", pveRepository)
		);
	}
}
