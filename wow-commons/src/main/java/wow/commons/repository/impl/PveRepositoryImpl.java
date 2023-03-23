package wow.commons.repository.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Zone;
import wow.commons.repository.PveRepository;
import wow.commons.repository.impl.parsers.pve.PveExcelParser;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@Repository
@RequiredArgsConstructor
public class PveRepositoryImpl extends ExcelRepository implements PveRepository {
	private final Map<Integer, Zone> zoneById = new HashMap<>();
	private final Map<String, Zone> zoneByName = new TreeMap<>();
	private final Map<Integer, Boss> bossById = new TreeMap<>();
	private final Map<String, Faction> factionByName = new TreeMap<>();

	@Value("${pve.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<Zone> getZone(int zoneId) {
		return Optional.ofNullable(zoneById.get(zoneId));
	}

	@Override
	public Optional<Zone> getZone(String name) {
		return Optional.ofNullable(zoneByName.get(name));
	}

	@Override
	public Optional<Boss> getBoss(int bossId) {
		return Optional.ofNullable(bossById.get(bossId));
	}

	@Override
	public Optional<Faction> getFaction(String name) {
		return Optional.ofNullable(factionByName.get(name));
	}

	@Override
	public List<Zone> getAllInstances() {
		return zoneByName.values().stream()
				.filter(Zone::isInstance)
				.toList();
	}

	@Override
	public List<Zone> getAllRaids() {
		return getAllInstances().stream()
				.filter(Zone::isRaid)
				.toList();
	}

	@PostConstruct
	public void init() throws IOException, InvalidFormatException {
		var pveExcelParser = new PveExcelParser(xlsFilePath, this);
		pveExcelParser.readFromXls();

		for (Zone instance : getAllInstances()) {
			instance.setBosses(new ArrayList<>());
		}

		for (Boss boss : bossById.values()) {
			for (Zone zone : boss.getZones()) {
				if (zone.isInstance()) {
					zone.getBosses().add(boss);
				}
			}
		}
	}

	public void addZone(Zone zone) {
		zoneById.put(zone.getId(), zone);
		zoneByName.put(zone.getName(), zone);
	}

	public void addBoss(Boss boss) {
		bossById.put(boss.getId(), boss);
	}

	public void addFactionByName(Faction faction) {
		factionByName.put(faction.getName(), faction);
	}
}
