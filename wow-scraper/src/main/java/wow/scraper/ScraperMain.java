package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.scraper.importers.item.EnchantImporter;
import wow.scraper.importers.item.GemImporter;
import wow.scraper.importers.item.ItemImporter;
import wow.scraper.importers.item.TradedItemImporter;

import java.io.IOException;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-26
 */
@Slf4j
public class ScraperMain extends ScraperTool {
	public static void main(String[] args) throws IOException {
		new ScraperMain().run();
	}

	@Override
	protected void run() throws IOException {
		ItemImporter itemImporter = new ItemImporter();
		EnchantImporter enchantImporter = new EnchantImporter();
		GemImporter gemImporter = new GemImporter();
		TradedItemImporter tradedItemImporter = new TradedItemImporter();

		var importers = List.of(
				itemImporter,
				enchantImporter,
				gemImporter,
				tradedItemImporter
		);

		for (var importer : importers) {
			importer.init(
					getScraperConfig(),
					getWowheadFetcher(),
					getItemDetailRepository(),
					getSpellDetailRepository(),
					getQuestInfoRepository()
			);
			importer.importAll();
		}
	}
}
