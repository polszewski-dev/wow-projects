package wow.scraper.exporters.pve.excel;

import wow.scraper.exporters.excel.ExcelSheetWriter;
import wow.scraper.model.JsonNpcDetails;
import wow.scraper.model.WowheadNpcClassification;

import java.util.Objects;

import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.NAME;
import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.REQ_VERSION;
import static wow.commons.repository.impl.parsers.pve.PveBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class NpcSheetWriter extends ExcelSheetWriter<JsonNpcDetails, PveBaseExcelBuilder> {
	public NpcSheetWriter(PveBaseExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		setHeader(ID);
		setHeader(NAME, 40);
		setHeader(REQ_VERSION);
		setHeader(NPC_TYPE);
		setHeader(NPC_BOSS);
		setHeader(NPC_ZONE);
	}

	@Override
	public void writeRow(JsonNpcDetails npc) {
		setValue(npc.getId());
		setValue(npc.getName());
		setValue(npc.getReqVersion());
		setValue(WowheadNpcClassification.fromCode(npc.getClassification()).getType());
		setValue(npc.getBoss());
		setValue(npc.getLocation(), Objects::toString);
	}
}
