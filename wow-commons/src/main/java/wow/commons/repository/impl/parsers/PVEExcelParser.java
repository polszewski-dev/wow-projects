package wow.commons.repository.impl.parsers;

import wow.commons.model.pve.*;
import wow.commons.model.unit.BaseStatInfo;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.CombatRatingInfo;
import wow.commons.model.unit.Race;
import wow.commons.repository.impl.PVERepositoryImpl;
import wow.commons.util.ExcelParser;

import java.io.InputStream;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public class PVEExcelParser extends ExcelParser {
	private final PVERepositoryImpl pveRepository;

	public PVEExcelParser(PVERepositoryImpl pveRepository) {
		this.pveRepository = pveRepository;
	}

	@Override
	protected InputStream getExcelInputStream() {
		return fromResourcePath("/xls/pve_data.xls");
	}

	@Override
	protected Stream<SheetReader> getSheetReaders() {
		return Stream.of(
				new SheetReader(SHEET_INSTANCES, this::readInstances, COL_INSTANCE_NO),
				new SheetReader(SHEET_BOSSES, this::readBosses, COL_BOSS_NO),
				new SheetReader(SHEET_FACTIONS, this::readFactions, COL_FACTION_NO),
				new SheetReader(SHEET_BASE_STATS, this::readBaseStats, COL_BS_LEVEL),
				new SheetReader(SHEET_COMBAT_RATINGS, this::readCombatRatings, COL_CR_LEVEL)
		);
	}

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

	private void readInstances() {
		Instance instance = getInstance();
		pveRepository.addInstanceByName(instance);
	}

	private Instance getInstance() {
		var no = getInteger(COL_INSTANCE_NO);
		var name = getString(COL_INSTANCE_NAME);
		var partySize = getInteger(COL_INSTANCE_PARTY_SIZE);
		var version = GameVersion.parse(getString(COL_INSTANCE_VERSION));
		var phase = getInteger(COL_INSTANCE_PHASE);
		var shortName = getOptionalString(COL_INSTANCE_SHORT_NAME).orElse(null);

		if (partySize > 5) {
			return new Raid(no, name, partySize, version, phase, shortName);
		} else if (partySize == 5) {
			return new Dungeon(no, name, version, phase, shortName);
		} else {
			throw new IllegalArgumentException("Party size: " + partySize);
		}
	}

	private void readBosses() {
		Boss boss = getBoss();
		pveRepository.addBossByName(boss);
	}

	private Boss getBoss() {
		var no = getInteger(COL_BOSS_NO);
		var name = getString(COL_BOSS_NAME);
		var instance = getString(COL_BOSS_INSTANCE);

		return new Boss(no, name, pveRepository.getInstance(instance));
	}

	private void readFactions() {
		Faction faction = getFaction();
		pveRepository.addFactionByName(faction);
	}

	private Faction getFaction() {
		var no = getInteger(COL_FACTION_NO);
		var name = getString(COL_FACTION_NAME);
		var version = GameVersion.parse(getString(COL_FACTION_VERSION));
		var phase = getInteger(COL_FACTION_PHASE);

		return new Faction(no, name, version, phase);
	}

	private void readBaseStats() {
		BaseStatInfo baseStatInfo = getBaseStatInfo();
		pveRepository.addBaseStatInfo(baseStatInfo);
	}

	private BaseStatInfo getBaseStatInfo() {
		var level = getInteger(COL_BS_LEVEL);
		var characterClass = CharacterClass.parse(getString(COL_BS_CLASS));
		var race = Race.parse(getString(COL_BS_RACE));
		var baseStr = getInteger(COL_BS_BASE_STR);
		var baseAgi = getInteger(COL_BS_BASE_AGI);
		var baseSta = getInteger(COL_BS_BASE_STA);
		var baseInt = getInteger(COL_BS_BASE_INT);
		var baseSpi = getInteger(COL_BS_BASE_SPI);
		var baseHP = getInteger(COL_BS_BASE_HP);
		var baseMana = getInteger(COL_BS_BASE_MANA);
		var baseSpellCrit = getDouble(COL_BS_BASE_SPELL_CRIT);
		var intPerCrit = getDouble(COL_BS_INT_PER_CRIT);

		return new BaseStatInfo(level, characterClass, race, baseStr, baseAgi, baseSta, baseInt, baseSpi, baseHP, baseMana, baseSpellCrit, intPerCrit);
	}

	private void readCombatRatings() {
		CombatRatingInfo combatRatingInfo = getCombatRatingInfo();
		pveRepository.addCombatRatingInfo(combatRatingInfo);
	}

	private CombatRatingInfo getCombatRatingInfo() {
		var level = getInteger(COL_CR_LEVEL);
		var spellCrit = getDouble(COL_CR_SPELL_CRIT);
		var spellHit = getDouble(COL_CR_SPELL_HIT);
		var spellHaste = getDouble(COL_CR_SPELL_HASTE);

		return new CombatRatingInfo(level, spellCrit, spellHit, spellHaste);
	}
}
