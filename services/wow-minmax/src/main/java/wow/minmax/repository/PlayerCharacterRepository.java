package wow.minmax.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import wow.minmax.model.persistent.PlayerCharacterPO;

/**
 * User: POlszewski
 * Date: 2024-10-22
 */
public interface PlayerCharacterRepository extends MongoRepository<PlayerCharacterPO, String> {
}
