package wow.commons.repository.impl.parsers.pve;

import wow.commons.model.unit.BaseStatInfo;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.Race;
import wow.commons.repository.impl.PVERepositoryImpl;
import wow.commons.repository.impl.parsers.excel.WowExcelSheetParser;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class BaseStatsSheetParser extends WowExcelSheetParser {
	private final ExcelColumn colLevel = column("level");
	private final ExcelColumn colClass = column("class");
	private final ExcelColumn colRace = column("race");
	private final ExcelColumn colBaseStr = column("base_str");
	private final ExcelColumn colBaseAgi = column("base_agi");
	private final ExcelColumn colBaseSta = column("base_sta");
	private final ExcelColumn colBaseInt = column("base_int");
	private final ExcelColumn colBaseSpi = column("base_spi");
	private final ExcelColumn colBaseHp = column("base_hp");
	private final ExcelColumn colBaseMana = column("base_mana");
	private final ExcelColumn colBaseSpellCrit = column("base_spell_crit");
	private final ExcelColumn colIntPerCrit = column("int_per_crit");

	private final PVERepositoryImpl pveRepository;

	public BaseStatsSheetParser(String sheetName, PVERepositoryImpl pveRepository) {
		super(sheetName);
		this.pveRepository = pveRepository;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colLevel;
	}

	@Override
	protected void readSingleRow() {
		BaseStatInfo baseStatInfo = getBaseStatInfo();
		pveRepository.addBaseStatInfo(baseStatInfo);
	}

	private BaseStatInfo getBaseStatInfo() {
		var level = colLevel.getInteger();
		var characterClass = colClass.getEnum(CharacterClass::parse);
		var race = Race.parse(colRace.getString());
		var baseStr = colBaseStr.getInteger();
		var baseAgi = colBaseAgi.getInteger();
		var baseSta = colBaseSta.getInteger();
		var baseInt = colBaseInt.getInteger();
		var baseSpi = colBaseSpi.getInteger();
		var baseHP = colBaseHp.getInteger();
		var baseMana = colBaseMana.getInteger();
		var baseSpellCrit = colBaseSpellCrit.getPercent();
		var intPerCrit = colIntPerCrit.getDouble();

		return new BaseStatInfo(level, characterClass, race, baseStr, baseAgi, baseSta, baseInt, baseSpi, baseHP, baseMana, baseSpellCrit, intPerCrit);
	}
}
