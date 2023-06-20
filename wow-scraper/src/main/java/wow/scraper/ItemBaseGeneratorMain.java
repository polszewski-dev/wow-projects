package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.scraper.exporters.item.EnchantExporter;
import wow.scraper.exporters.item.GemItemExporter;
import wow.scraper.exporters.item.ItemExporter;
import wow.scraper.exporters.item.TradedItemExporter;
import wow.scraper.exporters.item.excel.ItemBaseExcelBuilder;
import wow.scraper.parsers.WowheadSourceParser;
import wow.scraper.parsers.tooltip.AbstractTooltipParser;

import java.io.IOException;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
@Slf4j
public class ItemBaseGeneratorMain extends ScraperTool {
	public static void main(String[] args) throws IOException {
		new ItemBaseGeneratorMain().run();
	}

	@Override
	protected void run() throws IOException {
		WowheadSourceParser.configure(getScraperConfig());

		ItemBaseExcelBuilder builder = new ItemBaseExcelBuilder(getScraperConfig());
		builder.start();

		exportItemBase(builder);

		String itemFilePath = getScraperConfig().getDirectoryPath() + "/item_base.xls";
		builder.finish(itemFilePath);

		log.info("Saved to {}", itemFilePath);
		AbstractTooltipParser.reportUnmatchedLines(log);
	}

	private void exportItemBase(ItemBaseExcelBuilder builder) throws IOException {
		exportAll(
				builder,
				new TradedItemExporter(),
				new ItemExporter(),
				new EnchantExporter(),
				new GemItemExporter()
		);
	}
}
