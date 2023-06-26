package wow.commons.repository;

import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Zone;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public interface PveRepository {
	Optional<Zone> getZone(int zoneId, PhaseId phaseId);
	Optional<Zone> getZone(String name, PhaseId phaseId);
	Optional<Boss> getBoss(int bossId, PhaseId phaseId);
	Optional<Faction> getFaction(String name, PhaseId phaseId);

	List<Zone> getAllInstances(PhaseId phaseId);
	List<Zone> getAllRaids(PhaseId phaseId);
}
