package wow.minmax.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import wow.minmax.model.PlayerCharacterConfig;

/**
 * User: POlszewski
 * Date: 2024-10-22
 */
public interface PlayerCharacterConfigRepository extends MongoRepository<PlayerCharacterConfig, String> {
}
