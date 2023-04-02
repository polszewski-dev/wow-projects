package wow.character.repository.impl.parsers.character;

import wow.character.model.character.ArmorProfficiency;
import wow.character.model.character.CharacterClass;
import wow.character.model.character.GameVersion;
import wow.character.model.character.WeaponProfficiency;
import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.character.CharacterClassId;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public class CharacterClassSheetParser extends CharacterSheetParser {
	private final ExcelColumn colId = column("id");
	private final ExcelColumn colArmorProfficiencies = column("armor_profficiencies");
	private final ExcelColumn colWeaponProfficiencies = column("weapon_profficiencies");
	private final ExcelColumn colDualwield = column("dualwield");

	public CharacterClassSheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName, characterRepository);
	}

	@Override
	protected void readSingleRow() {
		for (GameVersion version : getVersions()) {
			CharacterClass characterClass = getCharacterClass(version);
			addCharacterClass(characterClass, version);
		}
	}

	private CharacterClass getCharacterClass(GameVersion version) {
		var id = colId.getEnum(CharacterClassId::parse);
		var description = getDescription();
		var armorProfficiencies = colArmorProfficiencies.getSet(ArmorProfficiency::parse);
		var weaponProfficiencies = colWeaponProfficiencies.getSet(WeaponProfficiency::parse);
		var dualwield = colDualwield.getBoolean();

		return new CharacterClass(id, description, armorProfficiencies, weaponProfficiencies, dualwield, version);
	}

	private void addCharacterClass(CharacterClass characterClass, GameVersion version) {
		version.getCharacterClasses().add(characterClass);
	}
}
