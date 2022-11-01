package wow.commons.repository;

import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Zone;
import wow.commons.model.unit.BaseStatInfo;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.CombatRatingInfo;
import wow.commons.model.unit.Race;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public interface PVERepository {
	Optional<Zone> getZone(int zoneId);
	Optional<Zone> getZone(String name);
	Optional<Boss> getBoss(String name);
	Optional<Faction> getFaction(String name);

	List<Zone> getAllInstances();
	List<Zone> getAllRaids();

	BaseStatInfo getBaseStats(CharacterClass characterClass, Race race, int level);
	CombatRatingInfo getCombatRatings(int level);
}
