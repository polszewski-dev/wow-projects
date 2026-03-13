package wow.minmax.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import wow.minmax.model.db.RaidConfig;

/**
 * User: POlszewski
 * Date: 2026-03-14
 */
public interface RaidConfigRepository extends MongoRepository<RaidConfig, String> {
}
