package wow.minmax.repository;

import wow.minmax.model.PlayerProfile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public interface PlayerProfileRepository {
	List<PlayerProfile> getPlayerProfileList();

	Optional<PlayerProfile> getPlayerProfile(UUID profileId);

	void saveProfile(PlayerProfile playerProfile);
}
