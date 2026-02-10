package wow.minmax.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import wow.minmax.model.db.PlayerConfig;

/**
 * User: POlszewski
 * Date: 2024-10-22
 */
public interface PlayerConfigRepository extends MongoRepository<PlayerConfig, String> {
}
