package wow.scraper;

import wow.scraper.exporter.item.EnchantExporter;
import wow.scraper.exporter.item.GemItemExporter;
import wow.scraper.exporter.item.ItemExporter;
import wow.scraper.exporter.item.TradedItemExporter;
import wow.scraper.exporter.item.excel.EnchantExcelBuilder;
import wow.scraper.exporter.item.excel.GemExcelBuilder;
import wow.scraper.exporter.item.excel.ItemExcelBuilder;
import wow.scraper.exporter.item.excel.TradedItemExcelBuilder;
import wow.scraper.parser.WowheadSourceParser;
import wow.scraper.parser.tooltip.AbstractTooltipParser;

/**
 * User: POlszewski
 * Date: 2022-10-29
 */
public class ItemBaseGeneratorMain extends ScraperTool {
	public static void main(String[] args) {
		new ItemBaseGeneratorMain().run();
	}

	@Override
	protected void run() {
		WowheadSourceParser.configure(getScraperContext());

		export("item/traded-items.xls", TradedItemExcelBuilder::new, new TradedItemExporter());
		export("item/items.xls", ItemExcelBuilder::new, new ItemExporter());
		export("item/enchants.xls", EnchantExcelBuilder::new, new EnchantExporter());
		export("item/gems.xls", GemExcelBuilder::new, new GemItemExporter());

		AbstractTooltipParser.reportUnmatchedLines();
	}
}
