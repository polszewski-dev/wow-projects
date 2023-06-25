package wow.scraper.repository;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonFactionDetails;

import java.io.IOException;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-25
 */
public interface FactionDetailRepository {
	List<JsonFactionDetails> getAll(GameVersionId gameVersion) throws IOException;
}
