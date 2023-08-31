package wow.character.repository.impl.parser.character;

import wow.character.model.character.*;
import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.spell.SpellSchool;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public class CharacterClassSheetParser extends CharacterSheetParser {
	private final ExcelColumn colArmorProfficiencies = column("armor_profficiencies");
	private final ExcelColumn colWeaponProfficiencies = column("weapon_profficiencies");
	private final ExcelColumn colDualwield = column("dualwield");
	private final ExcelColumn colSpellSchools = column("spell_schools");
	private final ExcelColumn colDefaultTemplate = column("default_template");

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
		var spellSchools = colSpellSchools.getList(SpellSchool::parse);
		var defaultTemplate = colDefaultTemplate.getEnum(CharacterTemplateId::parse, null);

		return new CharacterClass(id, description, armorProfficiencies, weaponProfficiencies, dualwield, spellSchools, defaultTemplate, version);
	}

	private void addCharacterClass(CharacterClass characterClass, GameVersion version) {
		version.getCharacterClasses().add(characterClass);
	}
}
