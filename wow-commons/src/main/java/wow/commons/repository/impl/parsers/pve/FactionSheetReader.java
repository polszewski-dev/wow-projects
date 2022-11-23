package wow.commons.repository.impl.parsers.pve;

import wow.commons.model.pve.Faction;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.Phase;
import wow.commons.repository.impl.PVERepositoryImpl;
import wow.commons.util.ExcelSheetReader;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class FactionSheetReader extends ExcelSheetReader {
	private final ExcelColumn colNo = column("no.");
	private final ExcelColumn colName = column("name");
	private final ExcelColumn colVersion = column("version");
	private final ExcelColumn colPhase = column("phase");

	private final PVERepositoryImpl pveRepository;

	public FactionSheetReader(String sheetName, PVERepositoryImpl pveRepository) {
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
		var version = GameVersion.parse(colVersion.getString());
		var phase = colPhase.getEnum(Phase::parse);

		return new Faction(no, name, version, phase);
	}
}
