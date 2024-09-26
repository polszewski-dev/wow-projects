package wow.commons.repository.impl.parser.character;

import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecialization;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.GameVersion;
import wow.commons.repository.impl.parser.excel.GameVersionedExcelSheetParser;
import wow.commons.repository.pve.GameVersionRepository;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public class ProfessionSpecSheetParser extends GameVersionedExcelSheetParser {
	private final ExcelColumn colProfession = column("profession");

	public ProfessionSpecSheetParser(String sheetName, GameVersionRepository gameVersionRepository) {
		super(sheetName, gameVersionRepository);
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
		var profession = version.getProfession(professionId).orElseThrow();

		return new ProfessionSpecialization(id, description, profession);
	}

	private void addSpec(ProfessionSpecialization professionSpec) {
		professionSpec.getProfession().getSpecializations().add(professionSpec);
	}
}
