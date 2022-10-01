package wow.commons.repository.impl.parsers;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import polszewski.excel.reader.ExcelReader;
import polszewski.excel.reader.PoiExcelReader;
import wow.commons.model.pve.*;
import wow.commons.model.unit.BaseStatInfo;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.CombatRatingInfo;
import wow.commons.model.unit.Race;
import wow.commons.repository.impl.PVERepositoryImpl;

import java.io.IOException;
import java.util.Map;

import static wow.commons.util.ExcelUtil.*;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public class PVEExcelParser {
	private static final String XLS_FILE_PATH = "/xls/pve_data.xls";

	private static final String SHEET_INSTANCES = "instances";
	private static final String SHEET_BOSSES = "bosses";
	private static final String SHEET_FACTIONS = "factions";
	private static final String SHEET_BASE_STATS = "base_stats";
	private static final String SHEET_COMBAT_RATINGS = "combat_ratings";

	private static final String COL_INSTANCE_NO = "no.";
	private static final String COL_INSTANCE_NAME = "name";
	private static final String COL_INSTANCE_PARTY_SIZE = "party_size";
	private static final String COL_INSTANCE_VERSION = "version";
	private static final String COL_INSTANCE_PHASE = "phase";
	private static final String COL_INSTANCE_SHORT_NAME = "short_name";

	private static final String COL_BOSS_NO = "no.";
	private static final String COL_BOSS_NAME = "name";
	private static final String COL_BOSS_INSTANCE = "instance";

	private static final String COL_FACTION_NO = "no.";
	private static final String COL_FACTION_NAME = "name";
	private static final String COL_FACTION_VERSION = "version";
	private static final String COL_FACTION_PHASE = "phase";

	private static final String COL_BS_LEVEL = "level";
	private static final String COL_BS_CLASS = "class";
	private static final String COL_BS_RACE = "race";
	private static final String COL_BS_BASE_STR = "base_str";
	private static final String COL_BS_BASE_AGI = "base_agi";
	private static final String COL_BS_BASE_STA = "base_sta";
	private static final String COL_BS_BASE_INT = "base_int";
	private static final String COL_BS_BASE_SPI = "base_spi";
	private static final String COL_BS_BASE_HP = "base_hp";
	private static final String COL_BS_BASE_MANA = "base_mana";
	private static final String COL_BS_BASE_SPELL_CRIT = "base_spell_crit";
	private static final String COL_BS_INT_PER_CRIT = "int_per_crit";

	private static final String COL_CR_LEVEL = "level";
	private static final String COL_CR_SPELL_CRIT = "spell_crit";
	private static final String COL_CR_SPELL_HIT = "spell_hit";
	private static final String COL_CR_SPELL_HASTE = "spell_haste";

	public static void readFromXls(PVERepositoryImpl pveRepository) throws IOException, InvalidFormatException {
		try (ExcelReader excelReader = new PoiExcelReader(PVEExcelParser.class.getResourceAsStream(XLS_FILE_PATH))) {
			while (excelReader.nextSheet()) {
				if (!excelReader.nextRow()) {
					continue;
				}

				switch (excelReader.getCurrentSheetName()) {
					case SHEET_INSTANCES:
						readInstances(excelReader, pveRepository);
						break;
					case SHEET_BOSSES:
						readBosses(excelReader, pveRepository);
						break;
					case SHEET_FACTIONS:
						readFactions(excelReader, pveRepository);
						break;
					case SHEET_BASE_STATS:
						readBaseStats(excelReader, pveRepository);
						break;
					case SHEET_COMBAT_RATINGS:
						readCombatRatings(excelReader, pveRepository);
						break;
					default:
						throw new IllegalArgumentException("Unknown sheet: " + excelReader.getCurrentSheetName());
				}
			}
		}
	}

	private static void readInstances(ExcelReader excelReader, PVERepositoryImpl pveRepository) {
		Map<String, Integer> header = getHeader(excelReader);

		while (excelReader.nextRow()) {
			if (getString(COL_INSTANCE_NO, excelReader, header) == null) {
				continue;
			}

			int no = getInteger(COL_INSTANCE_NO, excelReader, header);
			String name = getString(COL_INSTANCE_NAME, excelReader, header);
			int partySize = getInteger(COL_INSTANCE_PARTY_SIZE, excelReader, header);
			GameVersion version = GameVersion.parse(getString(COL_INSTANCE_VERSION, excelReader, header));
			int phase = getInteger(COL_INSTANCE_PHASE, excelReader, header);
			String shortName = getString(COL_INSTANCE_SHORT_NAME, excelReader, header);

			Instance instance;

			if (partySize > 5) {
				instance = new Raid(no, name, partySize, version, phase, shortName);
			} else if (partySize == 5) {
				instance = new Dungeon(no, name, version, phase, shortName);
			} else {
				throw new IllegalArgumentException("Party size: " + partySize);
			}

			pveRepository.addInstanceByName(instance);
		}
	}

	private static void readBosses(ExcelReader excelReader, PVERepositoryImpl pveRepository) {
		Map<String, Integer> header = getHeader(excelReader);

		while (excelReader.nextRow()) {
			if (getString(COL_BOSS_NO, excelReader, header) == null) {
				continue;
			}

			int no = getInteger(COL_BOSS_NO, excelReader, header);
			String name = getString(COL_BOSS_NAME, excelReader, header);
			String instance = getString(COL_BOSS_INSTANCE, excelReader, header);

			Boss boss = new Boss(no, name, pveRepository.getInstance(instance));
			pveRepository.addBossByName(boss);
		}
	}

	private static void readFactions(ExcelReader excelReader, PVERepositoryImpl pveRepository) {
		Map<String, Integer> header = getHeader(excelReader);

		while (excelReader.nextRow()) {
			if (getString(COL_FACTION_NO, excelReader, header) == null) {
				continue;
			}

			int no = getInteger(COL_FACTION_NO, excelReader, header);
			String name = getString(COL_FACTION_NAME, excelReader, header);
			GameVersion version = GameVersion.parse(getString(COL_FACTION_VERSION, excelReader, header));
			int phase = getInteger(COL_FACTION_PHASE, excelReader, header);

			Faction faction = new Faction(no, name, version, phase);
			pveRepository.addFactionByName(faction);
		}
	}

	private static void readBaseStats(ExcelReader excelReader, PVERepositoryImpl pveRepository) {
		Map<String, Integer> header = getHeader(excelReader);

		while (excelReader.nextRow()) {
			if (getString(COL_BS_LEVEL, excelReader, header) == null) {
				continue;
			}

			int level = getInteger(COL_BS_LEVEL, excelReader, header);
			CharacterClass characterClass = CharacterClass.parse(getString(COL_BS_CLASS, excelReader, header));
			Race race = Race.parse(getString(COL_BS_RACE, excelReader, header));
			int baseStr = getInteger(COL_BS_BASE_STR, excelReader, header);
			int baseAgi = getInteger(COL_BS_BASE_AGI, excelReader, header);
			int baseSta = getInteger(COL_BS_BASE_STA, excelReader, header);
			int baseInt = getInteger(COL_BS_BASE_INT, excelReader, header);
			int baseSpi = getInteger(COL_BS_BASE_SPI, excelReader, header);
			int baseHP = getInteger(COL_BS_BASE_HP, excelReader, header);
			int baseMana = getInteger(COL_BS_BASE_MANA, excelReader, header);
			double baseSpellCrit = getDouble(COL_BS_BASE_SPELL_CRIT, excelReader, header);
			double intPerCrit = getDouble(COL_BS_INT_PER_CRIT, excelReader, header);

			BaseStatInfo baseStatInfo = new BaseStatInfo(level, characterClass, race, baseStr, baseAgi, baseSta, baseInt, baseSpi, baseHP, baseMana, baseSpellCrit, intPerCrit);
			pveRepository.addBaseStatInfo(baseStatInfo);
		}
	}

	private static void readCombatRatings(ExcelReader excelReader, PVERepositoryImpl pveRepository) {
		Map<String, Integer> header = getHeader(excelReader);

		while (excelReader.nextRow()) {
			if (getString(COL_CR_LEVEL, excelReader, header) == null) {
				continue;
			}

			int level = getInteger(COL_CR_LEVEL, excelReader, header);
			double spellCrit = getDouble(COL_CR_SPELL_CRIT, excelReader, header);
			double spellHit = getDouble(COL_CR_SPELL_HIT, excelReader, header);
			double spellHaste = getDouble(COL_CR_SPELL_HASTE, excelReader, header);

			CombatRatingInfo combatRatingInfo = new CombatRatingInfo(level, spellCrit, spellHit, spellHaste);
			pveRepository.addCombatRatingInfo(combatRatingInfo);
		}
	}
}
