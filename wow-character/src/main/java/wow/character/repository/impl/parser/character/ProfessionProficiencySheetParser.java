package wow.character.repository.impl.parser.character;

import polszewski.excel.reader.templates.ExcelSheetParser;
import wow.character.model.character.GameVersion;
import wow.character.model.character.ProfessionProficiency;
import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.profession.ProfessionProficiencyId;
import wow.commons.model.profession.ProfessionType;

/**
 * User: POlszewski
 * Date: 2023-07-31
 */
public class ProfessionProficiencySheetParser extends CharacterSheetParser {
	private final ExcelColumn colId = column("id");
	private final ExcelColumn colMaxSkill = column("max_skill");
	private final ExcelColumn colReqLvl = column("req_lvl:");

	public ProfessionProficiencySheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName, characterRepository);
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
