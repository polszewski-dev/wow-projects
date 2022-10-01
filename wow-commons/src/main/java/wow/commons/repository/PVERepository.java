package wow.commons.repository;

import wow.commons.model.pve.Boss;
import wow.commons.model.pve.Faction;
import wow.commons.model.pve.Instance;
import wow.commons.model.pve.Raid;
import wow.commons.model.unit.BaseStatInfo;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.CombatRatingInfo;
import wow.commons.model.unit.Race;

import java.util.Collection;

/**
 * User: POlszewski
 * Date: 2021-03-14
 */
public interface PVERepository {
	Instance getInstance(String name);
	Boss getBoss(String name);
	Faction getFaction(String name);

	Collection<Instance> getAllInstances();
	Collection<Raid> getAllRaids();

	BaseStatInfo getBaseStats(CharacterClass characterClass, Race race, int level);
	CombatRatingInfo getCombatRatings(int level);
}
