package wow.commons.repository.impl.pve;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Zone;
import wow.commons.repository.impl.parser.pve.ZoneExcelParser;
import wow.commons.repository.pve.ZoneRepository;
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
@Component
@RequiredArgsConstructor
public class ZoneRepositoryImpl implements ZoneRepository {
	private final GameVersionMap<Integer, Zone> zoneById = new GameVersionMap<>();
	private final GameVersionMap<String, List<Zone>> zoneByName = new GameVersionMap<>();

	@Value("${zones.xls.file.path}")
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
		var pveExcelParser = new ZoneExcelParser(xlsFilePath, this);
		pveExcelParser.readFromXls();
	}

	public void addZone(Zone zone) {
		zone.setNpcs(new ArrayList<>());
		zoneById.put(zone.getGameVersionId(), zone.getId(), zone);
		zoneByName.computeIfAbsent(zone.getGameVersionId(), zone.getName(), x -> new ArrayList<>()).add(zone);
	}
}
