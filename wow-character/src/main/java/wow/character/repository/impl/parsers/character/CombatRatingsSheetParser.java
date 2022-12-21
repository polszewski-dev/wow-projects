package wow.character.repository.impl.parsers.character;

import wow.character.model.character.CombatRatingInfo;
import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class CombatRatingsSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colLevel = column("level");
	private final ExcelColumn colSpellCrit = column("spell_crit");
	private final ExcelColumn colSpellHit = column("spell_hit");
	private final ExcelColumn colSpellHaste = column("spell_haste");

	private final CharacterRepositoryImpl characterRepository;

	public CombatRatingsSheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName);
		this.characterRepository = characterRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colLevel;
	}

	@Override
	protected void readSingleRow() {
		CombatRatingInfo combatRatingInfo = getCombatRatingInfo();
		characterRepository.addCombatRatingInfo(combatRatingInfo);
	}

	private CombatRatingInfo getCombatRatingInfo() {
		var level = colLevel.getInteger();
		var timeRestriction = getTimeRestriction();
		var spellCrit = colSpellCrit.getDouble();
		var spellHit = colSpellHit.getDouble();
		var spellHaste = colSpellHaste.getDouble();

		return new CombatRatingInfo(level, timeRestriction, spellCrit, spellHit, spellHaste);
	}
}
