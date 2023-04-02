package wow.character.repository.impl.parsers.character;

import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.GameVersion;
import wow.character.repository.impl.CharacterRepositoryImpl;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class CombatRatingsSheetParser extends CharacterSheetParser {
	private final ExcelColumn colLevel = column("level");
	private final ExcelColumn colSpellCrit = column("spell_crit");
	private final ExcelColumn colSpellHit = column("spell_hit");
	private final ExcelColumn colSpellHaste = column("spell_haste");

	public CombatRatingsSheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName, characterRepository);
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colLevel;
	}

	@Override
	protected void readSingleRow() {
		for (GameVersion version : getVersions()) {
			CombatRatingInfo combatRatingInfo = getCombatRatingInfo(version);
			addCombatRatingInfo(combatRatingInfo);
		}
	}

	private CombatRatingInfo getCombatRatingInfo(GameVersion version) {
		var level = colLevel.getInteger();
		var spellCrit = colSpellCrit.getDouble();
		var spellHit = colSpellHit.getDouble();
		var spellHaste = colSpellHaste.getDouble();

		return new CombatRatingInfo(level, spellCrit, spellHit, spellHaste, version);
	}

	private void addCombatRatingInfo(CombatRatingInfo combatRatingInfo) {
		combatRatingInfo.getGameVersion().getCombatRatingInfos().add(combatRatingInfo);
	}
}
