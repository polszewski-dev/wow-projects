package wow.scraper.exporters.pve.excel;

import wow.scraper.exporters.excel.ExcelSheetWriter;
import wow.scraper.model.JsonBossDetails;

import java.util.Objects;

import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.NAME;
import static wow.commons.repository.impl.parsers.excel.CommonColumnNames.REQ_VERSION;
import static wow.commons.repository.impl.parsers.pve.PveBaseExcelColumnNames.BOSS_ZONE;
import static wow.commons.repository.impl.parsers.pve.PveBaseExcelColumnNames.ID;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class BossSheetWriter extends ExcelSheetWriter<JsonBossDetails, PveBaseExcelBuilder> {
	public BossSheetWriter(PveBaseExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		setHeader(ID);
		setHeader(NAME, 40);
		setHeader(REQ_VERSION);
		setHeader(BOSS_ZONE);
	}

	@Override
	public void writeRow(JsonBossDetails boss) {
		setValue(boss.getId());
		setValue(boss.getName());
		setValue(boss.getReqVersion());
		setValue(boss.getLocation(), Objects::toString);
	}
}
