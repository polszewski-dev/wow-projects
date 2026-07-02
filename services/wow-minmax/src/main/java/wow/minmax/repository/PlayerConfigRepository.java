package wow.minmax.repository;

import wow.minmax.model.db.PlayerConfig;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2024-10-22
 */
public interface PlayerConfigRepository {
	Optional<PlayerConfig> findById(String id);

	List<PlayerConfig> findAll();

	PlayerConfig save(PlayerConfig playerConfig);
}
