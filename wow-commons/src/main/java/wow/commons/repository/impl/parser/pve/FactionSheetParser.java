package wow.commons.repository.impl.parser.pve;

import wow.commons.model.pve.Faction;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.Side;
import wow.commons.repository.impl.PveRepositoryImpl;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;

import static wow.commons.repository.impl.parser.excel.CommonColumnNames.NAME;
import static wow.commons.repository.impl.parser.pve.PveBaseExcelColumnNames.*;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class FactionSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colId = column(ID);
	private final ExcelColumn colName = column(NAME);
	private final ExcelColumn colVersion = column(FACTION_VERSION);
	private final ExcelColumn colSide = column(FACTION_SIDE);

	private final PveRepositoryImpl pveRepository;

	public FactionSheetParser(String sheetName, PveRepositoryImpl pveRepository) {
		super(sheetName);
		this.pveRepository = pveRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colName;
	}

	@Override
	protected void readSingleRow() {
		Faction faction = getFaction();
		pveRepository.addFactionByName(faction);
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
