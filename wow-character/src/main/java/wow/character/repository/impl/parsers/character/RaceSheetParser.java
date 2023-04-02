package wow.character.repository.impl.parsers.character;

import wow.character.model.character.GameVersion;
import wow.character.model.character.Race;
import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.Side;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public class RaceSheetParser extends CharacterSheetParser {
	private final ExcelColumn colId = column("id");
	private final ExcelColumn colSide = column("side");
	private final ExcelColumn colClasses = column("classes");

	public RaceSheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName, characterRepository);
	}

	@Override
	protected void readSingleRow() {
		for (GameVersion version : getVersions()) {
			Race race = getRace(version);
			addRace(race, version);
		}
	}

	private Race getRace(GameVersion version) {
		var id = colId.getEnum(RaceId::parse);
		var description = getDescription();
		var side = colSide.getEnum(Side::parse);
		return new Race(id, description, side, version);
	}

	private void addRace(Race race, GameVersion version) {
		version.getRaces().add(race);

		var classIds = colClasses.getList(CharacterClassId::parse);

		for (CharacterClassId characterClassId : classIds) {
			var characterClass = version.getCharacterClass(characterClassId);
			characterClass.getRaces().add(race);
		}
	}
}
