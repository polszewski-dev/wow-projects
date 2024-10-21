package wow.character.repository.impl.parser.character;

import wow.character.model.character.CombatRatingInfo;
import wow.character.repository.impl.CombatRatingInfoRepositoryImpl;
import wow.commons.model.pve.GameVersion;
import wow.commons.repository.impl.parser.excel.GameVersionedExcelSheetParser;
import wow.commons.repository.pve.GameVersionRepository;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class CombatRatingInfoSheetParser extends GameVersionedExcelSheetParser {
	private final ExcelColumn colLevel = column("level");
	private final ExcelColumn colSpellCrit = column("spell_crit");
	private final ExcelColumn colSpellHit = column("spell_hit");
	private final ExcelColumn colSpellHaste = column("spell_haste");

	private final CombatRatingInfoRepositoryImpl combatRatingInfoRepository;

	public CombatRatingInfoSheetParser(String sheetName, GameVersionRepository gameVersionRepository, CombatRatingInfoRepositoryImpl combatRatingInfoRepository) {
		super(sheetName, gameVersionRepository);
		this.combatRatingInfoRepository = combatRatingInfoRepository;
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
		combatRatingInfoRepository.addCombatRatingInfo(combatRatingInfo);
	}
}
