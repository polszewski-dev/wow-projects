package wow.character.repository.impl.parsers.character;

import wow.character.model.character.Phase;
import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.pve.PhaseId;

/**
 * User: POlszewski
 * Date: 2023-03-30
 */
public class PhaseSheetParser extends CharacterSheetParser {
	private final ExcelColumn colId = column("id");

	public PhaseSheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName, characterRepository);
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colId;
	}

	@Override
	protected void readSingleRow() {
		Phase phase = getPhase();
		characterRepository.addPhase(phase);
	}

	private Phase getPhase() {
		var id = colId.getEnum(PhaseId::parse);
		var description = getDescription();
		var gameVersion = characterRepository.getGameVersion(id.getGameVersionId()).orElseThrow();

		var phase = new Phase(id, description, gameVersion);

		gameVersion.getPhases().add(phase);
		return phase;
	}
}