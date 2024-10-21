package wow.scraper.importer.item;

import wow.scraper.model.JsonItemDetails;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static wow.scraper.model.WowheadItemCategory.TOKENS;

/**
 * User: POlszewski
 * Date: 2023-06-25
 */
public class TokenImporter extends ItemImporter {
	public TokenImporter() {
		super(TOKENS);
	}

	@Override
	protected List<JsonItemDetails> fetchDetailsList(String url) {
		List<JsonItemDetails> result = super.fetchDetailsList(url);

		result.addAll(getWowheadFetcher().fetchItemDetails(getGameVersion(), "items", getIdsToFetch(result)));
		return result;
	}

	private List<Integer> getIdsToFetch(List<JsonItemDetails> result) {
		Set<Integer> existingIds = result.stream()
				.map(JsonItemDetails::getId)
				.collect(Collectors.toSet());

		return getScraperDatafixes().getTokenToTradedFor().keySet().stream()
				.filter(x -> !existingIds.contains(x))
				.toList();
	}
}
