package wow.scraper.exporter.pve.excel;

import wow.scraper.exporter.excel.ExcelSheetWriter;
import wow.scraper.model.JsonFactionDetails;
import wow.scraper.model.WowheadGameVersion;
import wow.scraper.model.WowheadSide;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.*;
import static wow.commons.repository.impl.parser.pve.PveBaseExcelColumnNames.FACTION_SIDE;
import static wow.commons.repository.impl.parser.pve.PveBaseExcelColumnNames.FACTION_VERSION;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class FactionSheetWriter extends ExcelSheetWriter<JsonFactionDetails, PveBaseExcelBuilder> {
	public FactionSheetWriter(PveBaseExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		setHeader(ID);
		setHeader(NAME, 40);
		setHeader(REQ_VERSION);
		setHeader(FACTION_VERSION);
		setHeader(FACTION_SIDE);
	}

	@Override
	public void writeRow(JsonFactionDetails faction) {
		setValue(faction.getId());
		setValue(faction.getName());
		setValue(faction.getReqVersion());
		setValue(WowheadGameVersion.fromCode(faction.getExpansion()));
		setValue(WowheadSide.fromCode(faction.getSide()).getSide());
	}
}
