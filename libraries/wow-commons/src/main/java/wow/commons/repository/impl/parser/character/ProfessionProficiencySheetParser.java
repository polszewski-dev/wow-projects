package wow.commons.repository.impl.parser.character;

import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.commons.model.profession.ProfessionProficiency;
import wow.commons.model.profession.ProfessionProficiencyId;
import wow.commons.model.profession.ProfessionType;
import wow.commons.model.pve.GameVersion;
import wow.commons.repository.impl.parser.excel.GameVersionedExcelSheetParser;
import wow.commons.repository.pve.GameVersionRepository;

/**
 * User: POlszewski
 * Date: 2023-07-31
 */
public class ProfessionProficiencySheetParser extends GameVersionedExcelSheetParser {
	private final ExcelColumn colMaxSkill = column("max_skill");
	private final ExcelColumn colReqLvl = column("req_lvl:");

	public ProfessionProficiencySheetParser(String sheetName, GameVersionRepository gameVersionRepository) {
		super(sheetName, gameVersionRepository);
	}

	@Override
	protected void readSingleRow() {
		for (GameVersion version : getVersions()) {
			ProfessionProficiency proficiency = getProfessionProficiency(version);
			addProficiency(proficiency, version);
		}
	}

	private ProfessionProficiency getProfessionProficiency(GameVersion version) {
		var id = colId.getEnum(ProfessionProficiencyId::parse);
		var description = getDescription();
		var maxSkill = colMaxSkill.getInteger();
		var reqLvl = colReqLvl.getMap(ProfessionType.values(), ExcelSheetParser.ExcelColumn::getInteger);

		return new ProfessionProficiency(id, description, maxSkill, reqLvl, version);
	}

	private void addProficiency(ProfessionProficiency proficiency, GameVersion version) {
		version.getProficiencies().add(proficiency);
	}
}
