package wow.scraper.repository;

import wow.commons.model.pve.GameVersion;
import wow.scraper.model.JsonItemDetailsAndTooltip;
import wow.scraper.model.WowheadItemCategory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-15
 */
public interface ItemDetailRepository {
	boolean hasItemDetail(GameVersion gameVersion, WowheadItemCategory category, int itemId);

	Optional<JsonItemDetailsAndTooltip> getDetail(GameVersion gameVersion, WowheadItemCategory category, int itemId) throws IOException;

	void saveItemDetail(GameVersion gameVersion, WowheadItemCategory category, int itemId, JsonItemDetailsAndTooltip detailsAndTooltip) throws IOException;

	List<Integer> getItemIds(GameVersion gameVersion, WowheadItemCategory category);
}
