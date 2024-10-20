package wow.commons.repository.impl.pve;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wow.commons.model.pve.Npc;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Zone;
import wow.commons.repository.impl.parser.pve.NpcExcelParser;
import wow.commons.repository.pve.NpcRepository;
import wow.commons.repository.pve.ZoneRepository;
import wow.commons.util.GameVersionMap;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@Component
@RequiredArgsConstructor
public class NpcRepositoryImpl implements NpcRepository {
	private final ZoneRepository zoneRepository;
	private final GameVersionMap<Integer, Npc> npcById = new GameVersionMap<>();

	@Value("${npcs.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<Npc> getNpc(int npcId, PhaseId phaseId) {
		return npcById.getOptional(phaseId.getGameVersionId(), npcId);
	}

	@PostConstruct
	public void init() throws IOException {
		var pveExcelParser = new NpcExcelParser(xlsFilePath, this, zoneRepository);
		pveExcelParser.readFromXls();
	}

	private void addNpcToZones(Npc npc) {
		for (Zone zone : npc.getZones()) {
			zone.getNpcs().add(npc);
		}
	}

	public void addNpc(Npc npc) {
		addNpcToZones(npc);
		npcById.put(npc.getTimeRestriction().getGameVersionId(), npc.getId(), npc);
	}
}
