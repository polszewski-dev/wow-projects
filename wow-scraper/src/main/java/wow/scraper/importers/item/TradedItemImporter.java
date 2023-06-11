package wow.scraper.importers.item;

import wow.scraper.importers.WowheadImporter;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.WowheadItemQuality;

import java.io.IOException;
import java.util.List;

import static wow.scraper.model.WowheadItemCategory.QUEST;
import static wow.scraper.model.WowheadItemCategory.TOKENS;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class TradedItemImporter extends WowheadImporter {
	@Override
	public void importAll() throws IOException {
		fetch("items/miscellaneous/armor-tokens", TOKENS);
		fetch("items/quest/quality:3:4:5?filter=6;1;0", QUEST);

		for (Integer tokenId : scraperConfig.getTokenToTradedFor().keySet()) {
			fetchMissingToken(tokenId);
		}
	}

	private void fetchMissingToken(Integer tokenId) {
		JsonItemDetails itemDetails = getDetails(tokenId);
		saveItemDetails(itemDetails, TOKENS);
	}

	private JsonItemDetails getDetails(Integer tokenId) {
		JsonItemDetails itemDetails = new JsonItemDetails();
		itemDetails.setId(tokenId);
		itemDetails.setName("" + tokenId);
		itemDetails.setSources(List.of());
		itemDetails.setQuality(WowheadItemQuality.EPIC.getCode());
		return itemDetails;
	}
}
