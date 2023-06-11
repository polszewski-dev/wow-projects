package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.scraper.importers.item.EnchantImporter;
import wow.scraper.importers.item.GemImporter;
import wow.scraper.importers.item.ItemImporter;
import wow.scraper.importers.item.TradedItemImporter;

import java.io.IOException;

/**
 * User: POlszewski
 * Date: 2022-10-26
 */
@Slf4j
public class ItemScraperMain extends ScraperTool {
	public static void main(String[] args) throws IOException {
		new ItemScraperMain().run();
	}

	@Override
	protected void run() throws IOException {
		importAll(
				new ItemImporter(),
				new EnchantImporter(),
				new GemImporter(),
				new TradedItemImporter()
		);
	}
}
