package wow.commons.repository.impl.parser.pve;

import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.Zone;
import wow.commons.model.pve.ZoneType;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.commons.repository.impl.pve.ZoneRepositoryImpl;

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

	private final ZoneRepositoryImpl zoneRepository;

	public ZoneSheetParser(String sheetName, ZoneRepositoryImpl zoneRepository) {
		super(sheetName);
		this.zoneRepository = zoneRepository;
	}

	@Override
	protected void readSingleRow() {
		Zone zone = getZone();
		zoneRepository.addZone(zone);
	}

	private Zone getZone() {
		var id = colId.getInteger();
		var name = colName.getString();
		var shortName = colShortName.getString(null);
		var type = colType.getEnum(ZoneType::valueOf);
		var version = colVersion.getEnum(GameVersionId::parse);
		var partySize = colPartySize.getInteger();
		var timeRestriction = getTimeRestriction();

		return new Zone(id, name, shortName, version, type, partySize, null, timeRestriction);
	}
}
