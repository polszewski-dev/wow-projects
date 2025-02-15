package wow.commons.repository.pve;

import wow.commons.model.pve.Faction;
import wow.commons.model.pve.PhaseId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public interface FactionRepository {
	Optional<Faction> getFaction(String name, PhaseId phaseId);
}
