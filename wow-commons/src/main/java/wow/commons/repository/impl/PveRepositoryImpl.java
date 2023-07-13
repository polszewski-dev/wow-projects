package wow.commons.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.PhaseId;
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
	private final Map<Integer, List<Zone>> zoneById = new HashMap<>();
	private final Map<String, List<Zone>> zoneByName = new TreeMap<>();
	private final Map<Integer, List<Boss>> bossById = new TreeMap<>();
	private final Map<String, List<Faction>> factionByName = new TreeMap<>();

	@Value("${pve.base.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<Zone> getZone(int zoneId, PhaseId phaseId) {
		return getUnique(zoneById, zoneId, phaseId);
	}

	@Override
	public Optional<Zone> getZone(String name, PhaseId phaseId) {
		return getUnique(zoneByName, name, phaseId);
	}

	@Override
	public Optional<Boss> getBoss(int bossId, PhaseId phaseId) {
		return getUnique(bossById, bossId, phaseId);
	}

	@Override
	public Optional<Faction> getFaction(String name, PhaseId phaseId) {
		return getUnique(factionByName, name, phaseId);
	}

	@Override
	public List<Zone> getAllInstances(PhaseId phaseId) {
		return zoneByName.values().stream()
				.flatMap(Collection::stream)
				.filter(x -> x.isAvailableDuring(phaseId))
				.filter(Zone::isInstance)
				.toList();
	}

	@Override
	public List<Zone> getAllRaids(PhaseId phaseId) {
		return getAllInstances(phaseId).stream()
				.filter(Zone::isRaid)
				.toList();
	}

	@PostConstruct
	public void init() throws IOException {
		var pveExcelParser = new PveExcelParser(xlsFilePath, this);
		pveExcelParser.readFromXls();

		zoneById.values().stream()
				.flatMap(Collection::stream)
				.filter(Zone::isInstance)
				.forEach(x -> x.setBosses(new ArrayList<>()));

		bossById.values().stream()
				.flatMap(Collection::stream)
				.forEach(this::fixInstance);
	}

	private void fixInstance(Boss boss) {
		for (Zone zone : boss.getZones()) {
			if (zone.isInstance()) {
				zone.getBosses().add(boss);
			}
		}
	}

	public void addZone(Zone zone) {
		addEntry(zoneById, zone.getId(), zone);
		addEntry(zoneByName, zone.getName(), zone);
	}

	public void addBoss(Boss boss) {
		addEntry(bossById, boss.getId(), boss);
	}

	public void addFactionByName(Faction faction) {
		addEntry(factionByName, faction.getName(), faction);
	}
}
