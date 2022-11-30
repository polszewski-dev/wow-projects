package wow.commons.repository.impl.parsers.pve;

import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.Zone;
import wow.commons.model.pve.ZoneType;
import wow.commons.repository.impl.PveRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class ZoneSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colId = column("id");
	private final ExcelColumn colName = column("name");
	private final ExcelColumn colShortName = column("short_name");
	private final ExcelColumn colType = column("type");
	private final ExcelColumn colVersion = column("version");
	private final ExcelColumn colPartySize = column("max_players");

	private final PveRepositoryImpl pveRepository;

	public ZoneSheetParser(String sheetName, PveRepositoryImpl pveRepository) {
		super(sheetName);
		this.pveRepository = pveRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colName;
	}

	@Override
	protected void readSingleRow() {
		Zone zone = getZone();
		pveRepository.addZone(zone);
	}

	private Zone getZone() {
		var id = colId.getInteger();
		var name = colName.getString();
		var shortName = colShortName.getString(null);
		var type = colType.getEnum(ZoneType::valueOf);
		var version = colVersion.getEnum(GameVersion::parse);
		var partySize = colPartySize.getInteger();

		return new Zone(id, name, shortName, version, type, partySize, null);
	}
}
