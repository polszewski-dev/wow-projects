package wow.character.repository.impl.parsers.character;

import wow.character.model.character.GameVersion;
import wow.character.model.character.Racial;
import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.character.RaceId;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public class RacialSheetParser extends CharacterSheetParser {
	private final ExcelColumn colRace = column("race");

	public RacialSheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName, characterRepository);
	}

	@Override
	protected void readSingleRow() {
		for (GameVersion version : getVersions()) {
			Racial racial = getRacial(version);
			addRacial(racial);
		}
	}

	private Racial getRacial(GameVersion version) {
		var description = getDescription();
		var timeRestriction = getTimeRestriction();
		var restriction = getRestriction();
		var raceId = colRace.getEnum(RaceId::parse);
		var race = version.getRace(raceId);
		var attributes = readAttributes();

		Racial racial = new Racial(description, timeRestriction, restriction, race);

		racial.setAttributes(attributes);
		return racial;
	}

	private void addRacial(Racial racial) {
		racial.getRace().getRacials().add(racial);
	}
}
