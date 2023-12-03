package wow.commons.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Npc;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Zone;
import wow.commons.repository.PveRepository;
import wow.commons.repository.impl.parser.pve.PveExcelParser;
import wow.commons.util.CollectionUtil;
import wow.commons.util.GameVersionMap;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@Repository
@RequiredArgsConstructor
public class PveRepositoryImpl implements PveRepository {
	private final GameVersionMap<Integer, Zone> zoneById = new GameVersionMap<>();
	private final GameVersionMap<String, List<Zone>> zoneByName = new GameVersionMap<>();
	private final GameVersionMap<Integer, Npc> npcById = new GameVersionMap<>();
	private final GameVersionMap<String, Faction> factionByName = new GameVersionMap<>();

	@Value("${pve.base.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<Zone> getZone(int zoneId, PhaseId phaseId) {
		return zoneById.getOptional(phaseId.getGameVersionId(), zoneId);
	}

	@Override
	public Optional<Zone> getZone(String name, PhaseId phaseId) {
		return zoneByName.getOptional(phaseId.getGameVersionId(), name)
				.flatMap(CollectionUtil::getUniqueResult);
	}

	@Override
	public Optional<Npc> getNpc(int npcId, PhaseId phaseId) {
		return npcById.getOptional(phaseId.getGameVersionId(), npcId);
	}

	@Override
	public Optional<Faction> getFaction(String name, PhaseId phaseId) {
		return factionByName.getOptional(phaseId.getGameVersionId(), name);
	}

	@Override
	public List<Zone> getAllInstances(PhaseId phaseId) {
		return zoneById.values(phaseId.getGameVersionId()).stream()
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
	}

	private void addNpcToZones(Npc npc) {
		for (Zone zone : npc.getZones()) {
			zone.getNpcs().add(npc);
		}
	}

	public void addZone(Zone zone) {
		zone.setNpcs(new ArrayList<>());
		zoneById.put(zone.getTimeRestriction().getGameVersionId(), zone.getId(), zone);
		zoneByName.computeIfAbsent(zone.getTimeRestriction().getGameVersionId(), zone.getName(), x -> new ArrayList<>()).add(zone);
	}

	public void addNpc(Npc npc) {
		addNpcToZones(npc);
		npcById.put(npc.getTimeRestriction().getGameVersionId(), npc.getId(), npc);
	}

	public void addFactionByName(Faction faction) {
		factionByName.put(faction.getTimeRestriction().getGameVersionId(), faction.getName(), faction);
	}
}
