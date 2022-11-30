package wow.commons.repository.impl.parsers.pve;

import wow.commons.model.pve.Boss;
import wow.commons.repository.impl.PveRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class BossSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colId = column("id");
	private final ExcelColumn colName = column("name");
	private final ExcelColumn colZone = column("zone");

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
		var zoneIds = colZone.getList(Integer::valueOf, ":");

		var zones = zoneIds.stream()
				.map(zoneId -> pveRepository.getZone(zoneId).orElseThrow())
				.collect(Collectors.toList());

		return new Boss(id, name, zones);
	}
}
