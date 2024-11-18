package wow.commons.repository.impl.parser.character;

import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.Race;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.Side;
import wow.commons.repository.impl.parser.excel.GameVersionedExcelSheetParser;
import wow.commons.repository.pve.GameVersionRepository;
import wow.commons.repository.spell.SpellRepository;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public class RaceSheetParser extends GameVersionedExcelSheetParser {
	private final ExcelColumn colSide = column("side");
	private final ExcelColumn colClasses = column("classes");

	private final SpellRepository spellRepository;

	public RaceSheetParser(String sheetName, GameVersionRepository gameVersionRepository, SpellRepository spellRepository) {
		super(sheetName, gameVersionRepository);
		this.spellRepository = spellRepository;
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
		var race = new Race(id, description, side, version);
		var racials = spellRepository.getRacialEffects(race.getRaceId(), version.getGameVersionId());

		race.getRacials().addAll(racials);
		return race;
	}

	private void addRace(Race race, GameVersion version) {
		version.getRaces().add(race);

		var classIds = colClasses.getList(CharacterClassId::parse);

		for (CharacterClassId characterClassId : classIds) {
			var characterClass = version.getCharacterClass(characterClassId).orElseThrow();
			characterClass.getRaces().add(race);
		}
	}
}
