package wow.scraper.exporter.pve.excel;

import wow.scraper.exporter.excel.ExcelSheetWriter;
import wow.scraper.model.JsonZoneDetails;
import wow.scraper.model.WowheadGameVersion;
import wow.scraper.model.WowheadZoneType;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.*;
import static wow.commons.repository.impl.parser.pve.PveBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2023-06-20
 */
public class ZoneSheetWriter extends ExcelSheetWriter<JsonZoneDetails, ZoneExcelBuilder> {
	public ZoneSheetWriter(ZoneExcelBuilder builder) {
		super(builder);
	}

	@Override
	public void writeHeader() {
		setHeader(ID);
		setHeader(NAME, 40);
		setHeader(ZONE_SHORT_NAME);
		setHeader(REQ_VERSION);
		setHeader(ZONE_VERSION);
		setHeader(ZONE_TYPE);
		setHeader(ZONE_MAX_PLAYERS);
		setHeader(ZONE_REQ_LVL);
		setHeader(ZONE_MIN_LVL);
		setHeader(ZONE_MAX_LVL);
	}

	@Override
	public void writeRow(JsonZoneDetails zone) {
		setValue(zone.getId());
		setValue(zone.getName());
		setValue(getShortName(zone));
		setValue(zone.getReqVersion());
		setValue(WowheadGameVersion.fromCode(zone.getExpansion()));
		setValue(WowheadZoneType.fromCode(zone.getInstance()).getType());
		setValue(zone.getNplayers());
		setValue(zone.getReqlevel());
		setValue(zone.getMinlevel());
		setValue(zone.getMaxlevel());
	}

	private String getShortName(JsonZoneDetails zone) {
		return datafixes.getDungeonShortNames().get(zone.getName());
	}
}
