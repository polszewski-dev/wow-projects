package wow.character.repository.impl.parser.character;

import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecialization;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.GameVersion;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public class ProfessionSpecSheetParser extends CharacterSheetParser {
	private final ExcelColumn colProfession = column("profession");

	public ProfessionSpecSheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName, characterRepository);
	}

	@Override
	protected void readSingleRow() {
		for (GameVersion version : getVersions()) {
			ProfessionSpecialization professionSpec = getProfessionSpec(version);
			addSpec(professionSpec);
		}
	}

	private ProfessionSpecialization getProfessionSpec(GameVersion version) {
		var id = colId.getEnum(ProfessionSpecializationId::parse);
		var description = getDescription();
		var professionId = colProfession.getEnum(ProfessionId::parse);
		var profession = version.getProfession(professionId);

		return new ProfessionSpecialization(id, description, profession);
	}

	private void addSpec(ProfessionSpecialization professionSpec) {
		professionSpec.getProfession().getSpecializations().add(professionSpec);
	}
}
