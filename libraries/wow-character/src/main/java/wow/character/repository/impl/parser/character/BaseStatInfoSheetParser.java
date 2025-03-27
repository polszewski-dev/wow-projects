package wow.character.repository.impl.parser.character;

import wow.character.model.character.BaseStatInfo;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.GameVersion;
import wow.commons.repository.impl.parser.excel.GameVersionedExcelSheetParser;
import wow.commons.repository.pve.GameVersionRepository;

import java.util.function.Consumer;

/**
 * User: POlszewski
 * Date: 2022-11-22
 */
public class BaseStatInfoSheetParser extends GameVersionedExcelSheetParser {
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

	private final Consumer<BaseStatInfo> consumer;

	public BaseStatInfoSheetParser(String sheetName, GameVersionRepository gameVersionRepository, Consumer<BaseStatInfo> consumer) {
		super(sheetName, gameVersionRepository);
		this.consumer = consumer;
	}

	@Override
	protected ExcelColumn getColumnIndicatingOptionalRow() {
		return colLevel;
	}

	@Override
	protected void readSingleRow() {
		for (var version : getVersions()) {
			var baseStatInfo = getBaseStatInfo(version);
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

		var characterClass = version.getCharacterClass(characterClassId).orElseThrow();
		var race = version.getRace(raceId).orElseThrow();

		return new BaseStatInfo(level, characterClass, race, baseStr, baseAgi, baseSta, baseInt, baseSpi, baseHP, baseMana, baseSpellCrit, intPerCrit, version);
	}

	private void addBaseStatInfo(BaseStatInfo baseStatInfo) {
		consumer.accept(baseStatInfo);
	}
}
