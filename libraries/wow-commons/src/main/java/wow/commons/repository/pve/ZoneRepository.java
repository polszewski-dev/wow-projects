package wow.commons.repository.pve;

import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Zone;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public interface ZoneRepository {
	Optional<Zone> getZone(int zoneId, PhaseId phaseId);

	Optional<Zone> getZone(String name, PhaseId phaseId);

	List<Zone> getAllInstances(PhaseId phaseId);

	List<Zone> getAllRaids(PhaseId phaseId);
}
