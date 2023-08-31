package wow.scraper.repository.impl;

import org.springframework.stereotype.Repository;
import wow.scraper.config.ScraperConfig;
import wow.scraper.fetcher.WowheadFetcher;
import wow.scraper.importer.item.*;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.repository.ItemDetailRepository;

import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-10-28
 */
@Repository
public class ItemDetailRepositoryImpl extends DetailRepository<WowheadItemCategory, JsonItemDetails, ItemImporter> implements ItemDetailRepository {
	public ItemDetailRepositoryImpl(ScraperConfig scraperConfig, WowheadFetcher wowheadFetcher) {
		super(scraperConfig, wowheadFetcher);
	}

	@Override
	protected Stream<ItemImporter> createImporters() {
		return Stream.concat(
				WowheadItemCategory.equipment().stream().map(ItemImporter::new),
				Stream.of(
						new GemImporter(),
						new TokenImporter(),
						new QuestItemImporter(),
						new PermanentEnchantImporter()
				)
		);
	}
}
