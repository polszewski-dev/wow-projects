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
	boolean hasDetail(GameVersionId gameVersion, WowheadItemCategory category, int itemId);

	Optional<JsonItemDetails> getDetail(GameVersionId gameVersion, WowheadItemCategory category, int itemId) throws IOException;

	void saveDetail(GameVersionId gameVersion, WowheadItemCategory category, int itemId, JsonItemDetails itemDetails) throws IOException;

	List<Integer> getItemIds(GameVersionId gameVersion, WowheadItemCategory category);
}
