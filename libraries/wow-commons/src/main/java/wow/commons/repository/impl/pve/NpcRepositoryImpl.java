package wow.commons.repository.impl.pve;

import org.springframework.stereotype.Component;
import wow.commons.model.pve.Npc;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.impl.parser.pve.NpcExcelParser;
import wow.commons.repository.pve.NpcRepository;
import wow.commons.util.GameVersionMap;

import java.io.IOException;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
@Component
public class NpcRepositoryImpl implements NpcRepository {
	private final GameVersionMap<Integer, Npc> npcById = new GameVersionMap<>();

	public NpcRepositoryImpl(NpcExcelParser parser) throws IOException {
		parser.readFromXls();
		parser.getNpcs().forEach(this::addNpc);
	}

	@Override
	public Optional<Npc> getNpc(int npcId, PhaseId phaseId) {
		return npcById.getOptional(phaseId.getGameVersionId(), npcId);
	}

	private void addNpc(Npc npc) {
		npcById.put(npc.getGameVersionId(), npc.getId(), npc);
	}
}
