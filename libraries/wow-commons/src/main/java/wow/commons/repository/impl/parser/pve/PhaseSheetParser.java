package wow.commons.repository.impl.parser.pve;

import wow.commons.model.profession.ProfessionProficiencyId;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.excel.WowExcelSheetParser;
import wow.commons.repository.pve.GameVersionRepository;

/**
 * User: POlszewski
 * Date: 2023-03-30
 */
public class PhaseSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colMaxLvl = column("max_lvl");
	private final ExcelColumn colMaxProficiency = column("max_proficiency");

	private final GameVersionRepository gameVersionRepository;
	private final PhaseExcelParser parser;

	public PhaseSheetParser(String sheetName, GameVersionRepository gameVersionRepository, PhaseExcelParser parser) {
		super(sheetName);
		this.gameVersionRepository = gameVersionRepository;
		this.parser = parser;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colId;
	}

	@Override
	protected void readSingleRow() {
		var phase = getPhase();
		parser.addPhase(phase);
	}

	private Phase getPhase() {
		var id = colId.getEnum(PhaseId::parse);
		var description = getDescription();
		var maxLvl = colMaxLvl.getInteger();
		var maxProficiency = colMaxProficiency.getEnum(ProfessionProficiencyId::parse);
		var gameVersion = gameVersionRepository.getGameVersion(id.getGameVersionId()).orElseThrow();

		var phase = new Phase(id, description, maxLvl, maxProficiency, gameVersion);

		gameVersion.getPhases().add(phase);
		return phase;
	}
}
