package wow.scraper.repository;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.WowheadItemCategory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
public interface ItemDetailRepository {
	Optional<JsonItemDetails> getDetail(GameVersionId gameVersion, WowheadItemCategory category, int itemId) throws IOException;

	List<Integer> getDetailIds(GameVersionId gameVersion, WowheadItemCategory category) throws IOException;
}
