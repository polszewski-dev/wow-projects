package wow.character.repository.impl.parsers.character;

import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.GameVersion;
import wow.character.repository.impl.CharacterRepositoryImpl;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class BaseStatsSheetParser extends CharacterSheetParser {
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

	public BaseStatsSheetParser(String sheetName, CharacterRepositoryImpl characterRepository) {
		super(sheetName, characterRepository);
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colLevel;
	}

	@Override
	protected void readSingleRow() {
		for (GameVersion version : getVersions()) {
			BaseStatInfo baseStatInfo = getBaseStatInfo(version);
			addBaseStatInfo(baseStatInfo);
		}
	}

	private BaseStatInfo getBaseStatInfo(GameVersion version) {
		var level = colLevel.getInteger();
		var characterClassId = colClass.getEnum(CharacterClassId::parse);
		var raceId = RaceId.parse(colRace.getString());
		var baseStr = colBaseStr.getInteger();
		var baseAgi = colBaseAgi.getInteger();
		var baseSta = colBaseSta.getInteger();
		var baseInt = colBaseInt.getInteger();
		var baseSpi = colBaseSpi.getInteger();
		var baseHP = colBaseHp.getInteger();
		var baseMana = colBaseMana.getInteger();
		var baseSpellCrit = colBaseSpellCrit.getPercent();
		var intPerCrit = colIntPerCrit.getDouble();

		var characterClass = version.getCharacterClass(characterClassId);
		var race = version.getRace(raceId);

		return new BaseStatInfo(level, characterClass, race, baseStr, baseAgi, baseSta, baseInt, baseSpi, baseHP, baseMana, baseSpellCrit, intPerCrit, version);
	}

	private void addBaseStatInfo(BaseStatInfo baseStatInfo) {
		baseStatInfo.getCharacterClass().getBaseStatInfos().add(baseStatInfo);
	}
}
