package wow.commons.repository.impl.parser.pve;

import wow.commons.model.pve.Npc;
import wow.commons.model.pve.NpcType;
import wow.commons.repository.impl.PveRepositoryImpl;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.NAME;
import static wow.commons.repository.impl.parser.pve.PveBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class NpcSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colId = column(ID);
	private final ExcelColumn colName = column(NAME);
	private final ExcelColumn colType = column(NPC_TYPE);
	private final ExcelColumn colBoss = column(NPC_BOSS);
	private final ExcelColumn colZone = column(NPC_ZONE);

	private final PveRepositoryImpl pveRepository;

	public NpcSheetParser(String sheetName, PveRepositoryImpl pveRepository) {
		super(sheetName);
		this.pveRepository = pveRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colName;
	}

	@Override
	protected void readSingleRow() {
		Npc npc = getNpc();
		pveRepository.addNpc(npc);
	}

	private Npc getNpc() {
		var id = colId.getInteger();
		var name = colName.getString();
		var type = colType.getEnum(NpcType::valueOf);
		var boss = colBoss.getBoolean();
		var zoneIds = colZone.getList(Integer::valueOf);
		var timeRestriction = getTimeRestriction();

		var zones = zoneIds.stream()
				.map(zoneId -> pveRepository.getZone(zoneId, timeRestriction.getUniqueVersion().getLastPhase()).orElseThrow())
				.toList();

		return new Npc(id, name, type, boss, zones, timeRestriction);
	}
}
