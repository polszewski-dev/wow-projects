package wow.commons.repository.impl.parsers.pve;

import wow.commons.repository.impl.PVERepositoryImpl;
import wow.commons.util.ExcelParser;
import wow.commons.util.ExcelSheetReader;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public class PVEExcelParser extends ExcelParser {
	private final PVERepositoryImpl pveRepository;

	public PVEExcelParser(PVERepositoryImpl pveRepository) {
		this.pveRepository = pveRepository;
	}

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath("/xls/pve_data.xls");
	}

	@Override
	protected Stream<ExcelSheetReader> getSheetReaders() {
		return Stream.of(
				new ZoneSheetReader("zones", pveRepository),
				new BossSheetReader("bosses", pveRepository),
				new FactionSheetReader("factions", pveRepository),
				new BaseStatsSheetReader("base_stats", pveRepository),
				new CombatRatingsSheetReader("combat_ratings", pveRepository)
		);
	}
}
