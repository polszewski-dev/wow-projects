package wow.scraper;

import lombok.extern.slf4j.Slf4j;
import wow.scraper.exporter.item.EnchantExporter;
import wow.scraper.exporter.item.GemItemExporter;
import wow.scraper.exporter.item.ItemExporter;
import wow.scraper.exporter.item.TradedItemExporter;
import wow.scraper.exporter.item.excel.ItemBaseExcelBuilder;
import wow.scraper.parser.WowheadSourceParser;
import wow.scraper.parser.tooltip.AbstractTooltipParser;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
@Slf4j
public class ItemBaseGeneratorMain extends ScraperTool {
	public static void main(String[] args) {
		new ItemBaseGeneratorMain().run();
	}

	@Override
	protected void run() {
		WowheadSourceParser.configure(getScraperContext());

		ItemBaseExcelBuilder builder = new ItemBaseExcelBuilder(getScraperConfig());
		builder.start();

		exportItemBase(builder);

		String itemFilePath = getScraperConfig().getDirectoryPath() + "/item_base.xls";
		builder.finish(itemFilePath);

		log.info("Saved to {}", itemFilePath);
		AbstractTooltipParser.reportUnmatchedLines(log);
	}

	private void exportItemBase(ItemBaseExcelBuilder builder) {
		exportAll(
				builder,
				new TradedItemExporter(),
				new ItemExporter(),
				new EnchantExporter(),
				new GemItemExporter()
		);
	}
}
