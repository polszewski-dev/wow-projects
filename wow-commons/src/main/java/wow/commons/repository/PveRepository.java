package wow.commons.repository;

import wow.commons.model.character.BaseStatInfo;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CombatRatingInfo;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Zone;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public interface PveRepository {
	Optional<Zone> getZone(int zoneId);
	Optional<Zone> getZone(String name);
	Optional<Boss> getBoss(int bossId);
	Optional<Faction> getFaction(String name);

	List<Zone> getAllInstances();
	List<Zone> getAllRaids();

	Optional<BaseStatInfo> getBaseStats(CharacterClass characterClass, Race race, int level);
	Optional<CombatRatingInfo> getCombatRatings(int level);
}
