package wow.minmax.repository.impl.mongo;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import wow.minmax.model.PlayerProfile;
import wow.minmax.repository.PlayerProfileRepository;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Profile({ "prod", "dev-mongo" })
public interface MongoPlayerProfileRepository extends PlayerProfileRepository, MongoRepository<PlayerProfile, String> {
}
