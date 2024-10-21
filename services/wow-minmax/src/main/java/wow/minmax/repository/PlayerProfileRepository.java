package wow.minmax.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import wow.minmax.model.persistent.PlayerProfilePO;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public interface PlayerProfileRepository extends MongoRepository<PlayerProfilePO, String> {
}
