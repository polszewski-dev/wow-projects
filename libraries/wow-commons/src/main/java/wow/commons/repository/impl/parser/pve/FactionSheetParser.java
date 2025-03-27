package wow.commons.repository.impl.parser.pve;

import wow.commons.model.pve.Faction;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.Side;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;

import static wow.commons.repository.impl.parser.pve.PveBaseExcelColumnNames.FACTION_SIDE;
import static wow.commons.repository.impl.parser.pve.PveBaseExcelColumnNames.FACTION_VERSION;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class FactionSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colVersion = column(FACTION_VERSION);
	private final ExcelColumn colSide = column(FACTION_SIDE);

	private final FactionExcelParser parser;

	public FactionSheetParser(String sheetName, FactionExcelParser parser) {
		super(sheetName);
		this.parser = parser;
	}

	@Override
	protected void readSingleRow() {
		var faction = getFaction();
		parser.addFaction(faction);
	}

	private Faction getFaction() {
		var id = colId.getInteger();
		var name = colName.getString();
		var version = GameVersionId.parse(colVersion.getString());
		var side = colSide.getEnum(Side::parse, null);
		var timeRestriction = getTimeRestriction();

		return new Faction(id, name, version, side, timeRestriction);
	}
}
