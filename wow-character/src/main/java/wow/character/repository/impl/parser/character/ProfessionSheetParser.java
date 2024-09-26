package wow.character.repository.impl.parser.character;

import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.profession.Profession;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionType;
import wow.commons.model.pve.GameVersion;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public class ProfessionSheetParser extends CharacterSheetParser {
	private final ExcelColumn colType = column("type");

	public ProfessionSheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName, characterRepository);
	}

	@Override
	protected void readSingleRow() {
		for (GameVersion version : getVersions()) {
			Profession profession = getProfession(version);
			addProfession(profession, version);
		}
	}

	private Profession getProfession(GameVersion version) {
		var id = colId.getEnum(ProfessionId::parse);
		var description = getDescription();
		var type = colType.getEnum(ProfessionType::parse);

		return new Profession(id, description, type, version);
	}

	private void addProfession(Profession profession, GameVersion version) {
		version.getProfessions().add(profession);
	}
}
