package wow.commons.repository.impl.parsers.pve;

import wow.commons.model.unit.CombatRatingInfo;
import wow.commons.repository.impl.PVERepositoryImpl;
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

	private final PVERepositoryImpl pveRepository;

	public CombatRatingsSheetParser(String sheetName, PVERepositoryImpl pveRepository) {
		super(sheetName);
		this.pveRepository = pveRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colLevel;
	}

	@Override
	protected void readSingleRow() {
		CombatRatingInfo combatRatingInfo = getCombatRatingInfo();
		pveRepository.addCombatRatingInfo(combatRatingInfo);
	}

	private CombatRatingInfo getCombatRatingInfo() {
		var level = colLevel.getInteger();
		var spellCrit = colSpellCrit.getDouble();
		var spellHit = colSpellHit.getDouble();
		var spellHaste = colSpellHaste.getDouble();

		return new CombatRatingInfo(level, spellCrit, spellHit, spellHaste);
	}
}
