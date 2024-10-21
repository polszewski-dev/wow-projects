package wow.minmax.repository;

import wow.minmax.model.persistent.PlayerProfilePO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public interface PlayerProfileRepository {
	List<PlayerProfilePO> getPlayerProfileList();

	Optional<PlayerProfilePO> getPlayerProfile(UUID profileId);

	void saveProfile(PlayerProfilePO playerProfile);
}
