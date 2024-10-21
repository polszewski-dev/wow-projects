package wow.commons.repository.pve;

import wow.commons.model.pve.Npc;
import wow.commons.model.pve.PhaseId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public interface NpcRepository {
	Optional<Npc> getNpc(int npcId, PhaseId phaseId);
}
