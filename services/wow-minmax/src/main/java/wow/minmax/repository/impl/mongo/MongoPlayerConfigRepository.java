package wow.minmax.repository.impl.mongo;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import wow.minmax.model.db.PlayerConfig;
import wow.minmax.repository.PlayerConfigRepository;

/**
 * User: POlszewski
 * Date: 2024-10-22
 */
@Profile({ "prod", "dev-mongo" })
public interface MongoPlayerConfigRepository extends PlayerConfigRepository, MongoRepository<PlayerConfig, String> {
}
