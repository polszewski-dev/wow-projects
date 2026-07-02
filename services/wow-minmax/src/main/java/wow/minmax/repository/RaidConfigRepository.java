package wow.minmax.repository;

import wow.minmax.model.db.RaidConfig;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2026-03-14
 */
public interface RaidConfigRepository {
	Optional<RaidConfig> findById(String id);

	RaidConfig save(RaidConfig raidConfig);
}
