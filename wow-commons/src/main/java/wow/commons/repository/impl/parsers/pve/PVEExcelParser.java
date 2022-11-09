package wow.commons.repository.impl.parsers.pve;

import wow.commons.model.pve.*;
import wow.commons.model.unit.BaseStatInfo;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.CombatRatingInfo;
import wow.commons.model.unit.Race;
import wow.commons.repository.impl.PVERepositoryImpl;
import wow.commons.util.ExcelParser;

import java.io.InputStream;
import java.util.stream.Collectors;
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
				new SheetReader("zones", this::readZones, colZoneId),
				new SheetReader("bosses", this::readBosses, colBossId),
				new SheetReader("factions", this::readFactions, colFactionNo),
				new SheetReader("base_stats", this::readBaseStats, colBsLevel),
				new SheetReader("combat_ratings", this::readCombatRatings, colCrLevel)
		);
	}

	private final ExcelColumn colZoneId = column("id");
	private final ExcelColumn colZoneName = column("name");
	private final ExcelColumn colZoneShortName = column("short_name");
	private final ExcelColumn colZoneType = column("type");
	private final ExcelColumn colZoneVersion = column("version");
	private final ExcelColumn colZonePartySize = column("max_players");
//	private final ExcelColumn colZoneReqLvl = column("req_lvl");
//	private final ExcelColumn colZoneMinLvl = column("min_lvl");
//	private final ExcelColumn colZoneMaxLvl = column("max_lvl");

	private final ExcelColumn colBossId = column("id");
	private final ExcelColumn colBossName = column("name");
	private final ExcelColumn colBossZone = column("zone");

	private final ExcelColumn colFactionNo = column("no.");
	private final ExcelColumn colFactionName = column("name");
	private final ExcelColumn colFactionVersion = column("version");
	private final ExcelColumn colFactionPhase = column("phase");

	private final ExcelColumn colBsLevel = column("level");
	private final ExcelColumn colBsClass = column("class");
	private final ExcelColumn colBsRace = column("race");
	private final ExcelColumn colBsBaseStr = column("base_str");
	private final ExcelColumn colBsBaseAgi = column("base_agi");
	private final ExcelColumn colBsBaseSta = column("base_sta");
	private final ExcelColumn colBsBaseInt = column("base_int");
	private final ExcelColumn colBsBaseSpi = column("base_spi");
	private final ExcelColumn colBsBaseHp = column("base_hp");
	private final ExcelColumn colBsBaseMana = column("base_mana");
	private final ExcelColumn colBsBaseSpellCrit = column("base_spell_crit");
	private final ExcelColumn colBsIntPerCrit = column("int_per_crit");

	private final ExcelColumn colCrLevel = column("level");
	private final ExcelColumn colCrSpellCrit = column("spell_crit");
	private final ExcelColumn colCrSpellHit = column("spell_hit");
	private final ExcelColumn colCrSpellHaste = column("spell_haste");

	private void readZones() {
		Zone zone = getZone();
		pveRepository.addZone(zone);
	}

	private Zone getZone() {
		var id = colZoneId.getInteger();
		var name = colZoneName.getString();
		var shortName = colZoneShortName.getString(null);
		var type = colZoneType.getEnum(ZoneType::valueOf);
		var version = colZoneVersion.getEnum(GameVersion::parse);
		var partySize = colZonePartySize.getInteger();
//		var reqLvl = colZoneReqLvl.getInteger();
//		var minLvl = colZoneMinLvl.getInteger();
//		var maxLvl = colZoneMaxLvl.getInteger();

		return new Zone(id, name, shortName, version, type, partySize);
	}

	private void readBosses() {
		Boss boss = getBoss();
		pveRepository.addBossByName(boss);
	}

	private Boss getBoss() {
		var id = colBossId.getInteger();
		var name = colBossName.getString();
		var zoneIds = colBossZone.getList(Integer::valueOf, ":");

		var zones = zoneIds.stream()
				.map(zoneId -> pveRepository.getZone(zoneId).orElseThrow())
				.collect(Collectors.toList());

		return new Boss(id, name, zones);
	}

	private void readFactions() {
		Faction faction = getFaction();
		pveRepository.addFactionByName(faction);
	}

	private Faction getFaction() {
		var no = colFactionNo.getInteger();
		var name = colFactionName.getString();
		var version = GameVersion.parse(colFactionVersion.getString());
		var phase = colFactionPhase.getEnum(Phase::parse);

		return new Faction(no, name, version, phase);
	}

	private void readBaseStats() {
		BaseStatInfo baseStatInfo = getBaseStatInfo();
		pveRepository.addBaseStatInfo(baseStatInfo);
	}

	private BaseStatInfo getBaseStatInfo() {
		var level = colBsLevel.getInteger();
		var characterClass = colBsClass.getEnum(CharacterClass::parse);
		var race = Race.parse(colBsRace.getString());
		var baseStr = colBsBaseStr.getInteger();
		var baseAgi = colBsBaseAgi.getInteger();
		var baseSta = colBsBaseSta.getInteger();
		var baseInt = colBsBaseInt.getInteger();
		var baseSpi = colBsBaseSpi.getInteger();
		var baseHP = colBsBaseHp.getInteger();
		var baseMana = colBsBaseMana.getInteger();
		var baseSpellCrit = colBsBaseSpellCrit.getDouble();
		var intPerCrit = colBsIntPerCrit.getDouble();

		return new BaseStatInfo(level, characterClass, race, baseStr, baseAgi, baseSta, baseInt, baseSpi, baseHP, baseMana, baseSpellCrit, intPerCrit);
	}

	private void readCombatRatings() {
		CombatRatingInfo combatRatingInfo = getCombatRatingInfo();
		pveRepository.addCombatRatingInfo(combatRatingInfo);
	}

	private CombatRatingInfo getCombatRatingInfo() {
		var level = colCrLevel.getInteger();
		var spellCrit = colCrSpellCrit.getDouble();
		var spellHit = colCrSpellHit.getDouble();
		var spellHaste = colCrSpellHaste.getDouble();

		return new CombatRatingInfo(level, spellCrit, spellHit, spellHaste);
	}
}
