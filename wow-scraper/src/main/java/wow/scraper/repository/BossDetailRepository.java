package wow.scraper.repository;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonBossDetails;

import java.io.IOException;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-06-25
 */
public interface BossDetailRepository {
	List<JsonBossDetails> getAll(GameVersionId gameVersion) throws IOException;
}
