package wow.commons.repository.impl.parsers.pve;

import wow.commons.model.pve.Boss;
import wow.commons.repository.impl.PveRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.NAME;
import static wow.commons.repository.impl.parsers.pve.PveBaseExcelColumnNames.BOSS_ZONE;
import static wow.commons.repository.impl.parsers.pve.PveBaseExcelColumnNames.ID;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class BossSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colId = column(ID);
	private final ExcelColumn colName = column(NAME);
	private final ExcelColumn colZone = column(BOSS_ZONE);

	private final PveRepositoryImpl pveRepository;

	public BossSheetParser(String sheetName, PveRepositoryImpl pveRepository) {
		super(sheetName);
		this.pveRepository = pveRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colName;
	}

	@Override
	protected void readSingleRow() {
		Boss boss = getBoss();
		pveRepository.addBoss(boss);
	}

	private Boss getBoss() {
		var id = colId.getInteger();
		var name = colName.getString();
		var zoneIds = colZone.getList(Integer::valueOf);
		var timeRestriction = getTimeRestriction();

		var zones = zoneIds.stream()
				.map(zoneId -> pveRepository.getZone(zoneId, timeRestriction.getUniqueVersion().getLastPhase()).orElseThrow())
				.toList();

		return new Boss(id, name, zones, timeRestriction);
	}
}
