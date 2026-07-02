package wow.minmax.repository;

import wow.minmax.model.PlayerProfile;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public interface PlayerProfileRepository {
	Optional<PlayerProfile> findById(String id);

	List<PlayerProfile> findAll();

	PlayerProfile save(PlayerProfile playerProfile);
}
