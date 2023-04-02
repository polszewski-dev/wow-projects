package wow.character.repository.impl.parsers.character;

import wow.character.model.character.GameVersion;
import wow.character.model.character.Profession;
import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.professions.ProfessionId;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public class ProfessionSheetParser extends CharacterSheetParser {
	private final ExcelColumn colId = column("id");

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

		return new Profession(id, description, version);
	}

	private void addProfession(Profession profession, GameVersion version) {
		version.getProfessions().add(profession);
	}
}
