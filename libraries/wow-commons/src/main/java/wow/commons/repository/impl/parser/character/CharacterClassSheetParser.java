package wow.commons.repository.impl.parser.character;

import wow.commons.model.character.ArmorProfficiency;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.WeaponProfficiency;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.spell.SpellSchool;
import wow.commons.repository.impl.parser.excel.GameVersionedExcelSheetParser;
import wow.commons.repository.pve.GameVersionRepository;

/**
 * User: POlszewski
 * Date: 2023-03-28
 */
public class CharacterClassSheetParser extends GameVersionedExcelSheetParser {
	private final ExcelColumn colArmorProfficiencies = column("armor_profficiencies");
	private final ExcelColumn colWeaponProfficiencies = column("weapon_profficiencies");
	private final ExcelColumn colDualwield = column("dualwield");
	private final ExcelColumn colSpellSchools = column("spell_schools");

	public CharacterClassSheetParser(String sheetName, GameVersionRepository gameVersionRepository) {
		super(sheetName, gameVersionRepository);
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

		return new CharacterClass(id, description, armorProfficiencies, weaponProfficiencies, dualwield, spellSchools, version);
	}

	private void addCharacterClass(CharacterClass characterClass, GameVersion version) {
		version.getCharacterClasses().add(characterClass);
	}
}
