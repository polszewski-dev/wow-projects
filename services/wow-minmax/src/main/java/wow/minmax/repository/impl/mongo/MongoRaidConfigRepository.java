package wow.minmax.repository.impl.mongo;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import wow.minmax.model.db.RaidConfig;
import wow.minmax.repository.RaidConfigRepository;

/**
 * User: POlszewski
 * Date: 2026-03-14
 */
@Profile({ "prod", "dev-mongo" })
public interface MongoRaidConfigRepository extends RaidConfigRepository, MongoRepository<RaidConfig, String> {
}
