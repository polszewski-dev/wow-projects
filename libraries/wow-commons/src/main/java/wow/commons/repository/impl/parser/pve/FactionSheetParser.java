package wow.commons.repository.impl.parser.pve;

import wow.commons.model.pve.Faction;
import wow.commons.model.pve.FactionExclusionGroupId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.Side;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;

import static wow.commons.repository.impl.parser.pve.PveBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class FactionSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colVersion = column(FACTION_VERSION);
	private final ExcelColumn colSide = column(FACTION_SIDE);
	private final ExcelColumn colExclusionGroup = column(FACTION_EXCLUSION_GROUP);

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
		var version = colVersion.getEnum(GameVersionId::parse);
		var side = colSide.getEnum(Side::parse, null);
		var exclusionGroup = colExclusionGroup.getEnum(FactionExclusionGroupId::parse, null);
		var timeRestriction = getTimeRestriction();

		return new Faction(id, name, version, side, exclusionGroup, timeRestriction);
	}
}
