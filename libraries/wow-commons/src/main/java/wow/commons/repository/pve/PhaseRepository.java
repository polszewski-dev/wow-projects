package wow.commons.repository.pve;

import wow.commons.model.pve.Phase;
import wow.commons.model.pve.PhaseId;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
public interface PhaseRepository {
	Optional<Phase> getPhase(PhaseId phaseId);
}
