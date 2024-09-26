package wow.character.repository.impl.parser.character;

import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.character.Pet;
import wow.commons.model.character.PetType;
import wow.commons.model.pve.GameVersion;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public class PetSheetParser extends CharacterSheetParser {
	public PetSheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName, characterRepository);
	}

	@Override
	protected void readSingleRow() {
		for (GameVersion version : getVersions()) {
			Pet pet = getPet(version);
			addPet(pet, version);
		}
	}

	private Pet getPet(GameVersion version) {
		var id = colId.getEnum(PetType::parse);
		var description = getDescription();
		var restriction = getRestriction();

		return new Pet(id, description, restriction, version);
	}

	private void addPet(Pet pet, GameVersion version) {
		version.getPets().add(pet);
	}
}
