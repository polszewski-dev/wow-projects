package wow.commons.repository.impl.parser.pve;

import wow.commons.model.pve.Npc;
import wow.commons.model.pve.NpcType;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.commons.repository.impl.pve.NpcRepositoryImpl;
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

	private final NpcRepositoryImpl npcRepository;
	private final ZoneRepository zoneRepository;

	public NpcSheetParser(String sheetName, NpcRepositoryImpl npcRepository, ZoneRepository zoneRepository) {
		super(sheetName);
		this.npcRepository = npcRepository;
		this.zoneRepository = zoneRepository;
	}

	@Override
	protected void readSingleRow() {
		Npc npc = getNpc();
		npcRepository.addNpc(npc);
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
