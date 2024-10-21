package wow.scraper.repository;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonNpcDetails;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-06-25
 */
public interface NpcDetailRepository {
	List<JsonNpcDetails> getAll(GameVersionId gameVersion);

	Optional<JsonNpcDetails> getById(GameVersionId gameVersion, int id);
}
