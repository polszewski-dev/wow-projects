package wow.commons.repository.impl.parsers.pve;

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
				new SheetReader("instances", this::readInstances, COL_INSTANCE_NO),
				new SheetReader("bosses", this::readBosses, COL_BOSS_NO),
				new SheetReader("factions", this::readFactions, COL_FACTION_NO),
				new SheetReader("base_stats", this::readBaseStats, COL_BS_LEVEL),
				new SheetReader("combat_ratings", this::readCombatRatings, COL_CR_LEVEL)
		);
	}

	private final ExcelColumn COL_INSTANCE_NO = column("no.");
	private final ExcelColumn COL_INSTANCE_NAME = column("name");
	private final ExcelColumn COL_INSTANCE_PARTY_SIZE = column("party_size");
	private final ExcelColumn COL_INSTANCE_VERSION = column("version");
	private final ExcelColumn COL_INSTANCE_PHASE = column("phase");
	private final ExcelColumn COL_INSTANCE_SHORT_NAME = column("short_name");

	private final ExcelColumn COL_BOSS_NO = column("no.");
	private final ExcelColumn COL_BOSS_NAME = column("name");
	private final ExcelColumn COL_BOSS_INSTANCE = column("instance");

	private final ExcelColumn COL_FACTION_NO = column("no.");
	private final ExcelColumn COL_FACTION_NAME = column("name");
	private final ExcelColumn COL_FACTION_VERSION = column("version");
	private final ExcelColumn COL_FACTION_PHASE = column("phase");

	private final ExcelColumn COL_BS_LEVEL = column("level");
	private final ExcelColumn COL_BS_CLASS = column("class");
	private final ExcelColumn COL_BS_RACE = column("race");
	private final ExcelColumn COL_BS_BASE_STR = column("base_str");
	private final ExcelColumn COL_BS_BASE_AGI = column("base_agi");
	private final ExcelColumn COL_BS_BASE_STA = column("base_sta");
	private final ExcelColumn COL_BS_BASE_INT = column("base_int");
	private final ExcelColumn COL_BS_BASE_SPI = column("base_spi");
	private final ExcelColumn COL_BS_BASE_HP = column("base_hp");
	private final ExcelColumn COL_BS_BASE_MANA = column("base_mana");
	private final ExcelColumn COL_BS_BASE_SPELL_CRIT = column("base_spell_crit");
	private final ExcelColumn COL_BS_INT_PER_CRIT = column("int_per_crit");

	private final ExcelColumn COL_CR_LEVEL = column("level");
	private final ExcelColumn COL_CR_SPELL_CRIT = column("spell_crit");
	private final ExcelColumn COL_CR_SPELL_HIT = column("spell_hit");
	private final ExcelColumn COL_CR_SPELL_HASTE = column("spell_haste");

	private void readInstances() {
		Instance instance = getInstance();
		pveRepository.addInstanceByName(instance);
	}

	private Instance getInstance() {
		var no = COL_INSTANCE_NO.getInteger();
		var name = COL_INSTANCE_NAME.getString();
		var partySize = COL_INSTANCE_PARTY_SIZE.getInteger();
		var version = GameVersion.parse(COL_INSTANCE_VERSION.getString());
		var phase = COL_INSTANCE_PHASE.getEnum(Phase::parse);
		var shortName = COL_INSTANCE_SHORT_NAME.getString(null);

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
		var no = COL_BOSS_NO.getInteger();
		var name = COL_BOSS_NAME.getString();
		var instance = COL_BOSS_INSTANCE.getString();

		return new Boss(no, name, pveRepository.getInstance(instance));
	}

	private void readFactions() {
		Faction faction = getFaction();
		pveRepository.addFactionByName(faction);
	}

	private Faction getFaction() {
		var no = COL_FACTION_NO.getInteger();
		var name = COL_FACTION_NAME.getString();
		var version = GameVersion.parse(COL_FACTION_VERSION.getString());
		var phase = COL_FACTION_PHASE.getEnum(Phase::parse);

		return new Faction(no, name, version, phase);
	}

	private void readBaseStats() {
		BaseStatInfo baseStatInfo = getBaseStatInfo();
		pveRepository.addBaseStatInfo(baseStatInfo);
	}

	private BaseStatInfo getBaseStatInfo() {
		var level = COL_BS_LEVEL.getInteger();
		var characterClass = CharacterClass.parse(COL_BS_CLASS.getString());
		var race = Race.parse(COL_BS_RACE.getString());
		var baseStr = COL_BS_BASE_STR.getInteger();
		var baseAgi = COL_BS_BASE_AGI.getInteger();
		var baseSta = COL_BS_BASE_STA.getInteger();
		var baseInt = COL_BS_BASE_INT.getInteger();
		var baseSpi = COL_BS_BASE_SPI.getInteger();
		var baseHP = COL_BS_BASE_HP.getInteger();
		var baseMana = COL_BS_BASE_MANA.getInteger();
		var baseSpellCrit = COL_BS_BASE_SPELL_CRIT.getDouble();
		var intPerCrit = COL_BS_INT_PER_CRIT.getDouble();

		return new BaseStatInfo(level, characterClass, race, baseStr, baseAgi, baseSta, baseInt, baseSpi, baseHP, baseMana, baseSpellCrit, intPerCrit);
	}

	private void readCombatRatings() {
		CombatRatingInfo combatRatingInfo = getCombatRatingInfo();
		pveRepository.addCombatRatingInfo(combatRatingInfo);
	}

	private CombatRatingInfo getCombatRatingInfo() {
		var level = COL_CR_LEVEL.getInteger();
		var spellCrit = COL_CR_SPELL_CRIT.getDouble();
		var spellHit = COL_CR_SPELL_HIT.getDouble();
		var spellHaste = COL_CR_SPELL_HASTE.getDouble();

		return new CombatRatingInfo(level, spellCrit, spellHit, spellHaste);
	}
}
