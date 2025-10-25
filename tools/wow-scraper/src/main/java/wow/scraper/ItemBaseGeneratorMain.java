package wow.scraper;

import wow.scraper.exporter.item.*;
import wow.scraper.exporter.item.excel.*;
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
		export("item/item-sets.xls", ItemSetExcelBuilder::new, new ItemSetExporter());
		export("item/enchants.xls", EnchantExcelBuilder::new, new EnchantExporter());
		export("item/gems.xls", GemExcelBuilder::new, new GemItemExporter());

		AbstractTooltipParser.reportUnmatchedLines();
	}
}
