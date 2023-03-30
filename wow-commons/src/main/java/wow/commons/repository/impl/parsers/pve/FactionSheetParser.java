package wow.commons.repository.impl.parsers.pve;

import wow.commons.model.pve.Faction;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.PveRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class FactionSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colNo = column("no.");
	private final ExcelColumn colName = column("name");
	private final ExcelColumn colVersion = column("version");
	private final ExcelColumn colPhase = column("phase");

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
		var no = colNo.getInteger();
		var name = colName.getString();
		var version = GameVersionId.parse(colVersion.getString());
		var phase = colPhase.getEnum(PhaseId::parse);

		return new Faction(no, name, version, phase);
	}
}
