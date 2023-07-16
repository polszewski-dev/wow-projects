package wow.commons.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Npc;
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
	private final Map<Integer, List<Npc>> npcById = new TreeMap<>();
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
	public Optional<Npc> getNpc(int npcId, PhaseId phaseId) {
		return getUnique(npcById, npcId, phaseId);
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
		addNpcsToZones();
	}

	private void addNpcsToZones() {
		zoneById.values().stream()
				.flatMap(Collection::stream)
				.forEach(x -> x.setNpcs(new ArrayList<>()));

		npcById.values().stream()
				.flatMap(Collection::stream)
				.forEach(this::addNpcToZones);
	}

	private void addNpcToZones(Npc npc) {
		for (Zone zone : npc.getZones()) {
			zone.getNpcs().add(npc);
		}
	}

	public void addZone(Zone zone) {
		addEntry(zoneById, zone.getId(), zone);
		addEntry(zoneByName, zone.getName(), zone);
	}

	public void addNpc(Npc npc) {
		addEntry(npcById, npc.getId(), npc);
	}

	public void addFactionByName(Faction faction) {
		addEntry(factionByName, faction.getName(), faction);
	}
}
