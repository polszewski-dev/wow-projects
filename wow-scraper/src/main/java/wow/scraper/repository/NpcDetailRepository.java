package wow.scraper.repository;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonNpcDetails;

import java.io.IOException;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-25
 */
public interface NpcDetailRepository {
	List<JsonNpcDetails> getAll(GameVersionId gameVersion) throws IOException;
}
