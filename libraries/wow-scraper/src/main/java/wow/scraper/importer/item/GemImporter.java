package wow.scraper.importer.item;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonItemDetails;

import java.util.List;

import static wow.scraper.model.WowheadItemCategory.GEMS;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class GemImporter extends ItemImporter {
	public GemImporter() {
		super(GEMS);
	}

	@Override
	protected List<JsonItemDetails> fetchDetailsList(String url) {
		if (getGameVersion() == GameVersionId.VANILLA) {
			return List.of();
		}
		return super.fetchDetailsList(url);
	}
}
