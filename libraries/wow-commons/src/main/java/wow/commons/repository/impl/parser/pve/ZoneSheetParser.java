package wow.commons.repository.impl.parser.pve;

import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.Zone;
import wow.commons.model.pve.ZoneType;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;

import java.util.ArrayList;

import static wow.commons.repository.impl.parser.pve.PveBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class ZoneSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colShortName = column(ZONE_SHORT_NAME);
	private final ExcelColumn colType = column(ZONE_TYPE);
	private final ExcelColumn colVersion = column(ZONE_VERSION);
	private final ExcelColumn colPartySize = column(ZONE_MAX_PLAYERS);

	private final ZoneExcelParser parser;

	public ZoneSheetParser(String sheetName, ZoneExcelParser parser) {
		super(sheetName);
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		var zone = getZone();
		parser.addZone(zone);
	}

	private Zone getZone() {
		var id = colId.getInteger();
		var name = colName.getString();
		var shortName = colShortName.getString(null);
		var type = colType.getEnum(ZoneType::valueOf);
		var version = colVersion.getEnum(GameVersionId::parse);
		var partySize = colPartySize.getInteger();
		var timeRestriction = getTimeRestriction();

		return new Zone(id, name, shortName, version, type, partySize, new ArrayList<>(), timeRestriction);
	}
}
