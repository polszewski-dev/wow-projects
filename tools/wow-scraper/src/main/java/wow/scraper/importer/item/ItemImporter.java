package wow.scraper.importer.item;

import wow.scraper.importer.WowheadImporter;
import wow.scraper.model.*;

import java.util.List;
import java.util.stream.IntStream;

import static wow.scraper.model.WowheadItemCategory.GEMS;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class ItemImporter extends WowheadImporter<WowheadItemCategory, JsonItemDetails> {
	public ItemImporter(WowheadItemCategory category) {
		super(category.getUrl(), category);
	}

	@Override
	protected List<JsonItemDetails> fetchDetailsList(String url) {
		return getWowheadFetcher().fetchItemDetails(getGameVersion(), url);
	}

	@Override
	protected boolean isToBeSaved(JsonItemDetails details) {
		if (details.getSources() == null) {
			return false;
		}
		if (getCategory().isEquipment() && details.getLevel() != null && details.getLevel() < getScraperConfig().getMinItemLevel(getGameVersion())) {
			return false;
		}
		if ((getCategory().isEquipment() || getCategory() == GEMS) && details.getQuality() < getScraperConfig().getMinQuality(getGameVersion()).getCode()) {
			return false;
		}
		return !getScraperDatafixes().getIgnoredItemIds().contains(details.getId());
	}

	@Override
	protected void beforeSave(JsonItemDetails details) {
		fixSource(details);
		fetchTooltip(details);
		details.setCategory(getCategory());
	}

	private void fixSource(JsonItemDetails itemDetails) {
		List<Integer> sources = itemDetails.getSources();

		if (sources.size() <= 1) {
			return;
		}

		tryReplaceSourcesWithCrafted(itemDetails, sources);
	}

	private boolean tryReplaceSourcesWithCrafted(JsonItemDetails itemDetails, List<Integer> sources) {
		int craftedPosition = IntStream.iterate(0, i -> i < sources.size(), i -> i + 1)
				.filter(i -> sources.get(i) == WowheadSource.CRAFTED.getCode())
				.findFirst().orElse(-1);

		if (craftedPosition < 0) {
			return false;
		}

		JsonSourceMore sourceMore = itemDetails.getSourceMores().get(craftedPosition);

		itemDetails.getSources().clear();
		itemDetails.getSources().add(WowheadSource.CRAFTED.getCode());
		itemDetails.getSourceMores().clear();
		itemDetails.getSourceMores().add(sourceMore);

		return true;
	}

	private void fetchTooltip(JsonItemDetails details) {
		WowheadItemInfo info = getWowheadFetcher().fetchItemTooltip(getGameVersion(), details.getId());
		details.setHtmlTooltip(fixTooltip(info.getTooltip()));
		details.setIcon(info.getIcon());
	}
}
