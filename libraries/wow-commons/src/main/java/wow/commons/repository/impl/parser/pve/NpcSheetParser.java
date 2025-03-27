package wow.commons.repository.impl.parser.pve;

import wow.commons.model.pve.Npc;
import wow.commons.model.pve.NpcType;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.commons.repository.pve.ZoneRepository;

import static wow.commons.repository.impl.parser.pve.PveBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class NpcSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colType = column(NPC_TYPE);
	private final ExcelColumn colBoss = column(NPC_BOSS);
	private final ExcelColumn colZone = column(NPC_ZONE);

	private final ZoneRepository zoneRepository;
	private final NpcExcelParser parser;

	public NpcSheetParser(String sheetName, ZoneRepository zoneRepository, NpcExcelParser parser) {
		super(sheetName);
		this.zoneRepository = zoneRepository;
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		var npc = getNpc();
		parser.addNpc(npc);
	}

	private Npc getNpc() {
		var id = colId.getInteger();
		var name = colName.getString();
		var type = colType.getEnum(NpcType::valueOf);
		var boss = colBoss.getBoolean();
		var zoneIds = colZone.getList(Integer::valueOf);
		var timeRestriction = getTimeRestriction();

		var zones = zoneIds.stream()
				.map(zoneId -> zoneRepository.getZone(zoneId, timeRestriction.earliestPhaseId()).orElseThrow())
				.toList();

		return new Npc(id, name, type, boss, zones, timeRestriction);
	}
}
