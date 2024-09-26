package wow.commons.repository.impl.parser.character;

import wow.commons.model.profession.Profession;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionType;
import wow.commons.model.pve.GameVersion;
import wow.commons.repository.impl.parser.excel.GameVersionedExcelSheetParser;
import wow.commons.repository.pve.GameVersionRepository;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public class ProfessionSheetParser extends GameVersionedExcelSheetParser {
	private final ExcelColumn colType = column("type");

	public ProfessionSheetParser(String sheetName, GameVersionRepository gameVersionRepository) {
		super(sheetName, gameVersionRepository);
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
