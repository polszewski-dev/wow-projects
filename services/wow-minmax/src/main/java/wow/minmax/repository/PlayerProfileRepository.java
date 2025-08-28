package wow.minmax.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import wow.minmax.model.PlayerProfile;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
public interface PlayerProfileRepository extends MongoRepository<PlayerProfile, String> {
}
