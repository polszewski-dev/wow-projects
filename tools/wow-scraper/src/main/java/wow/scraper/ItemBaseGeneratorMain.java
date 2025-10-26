package wow.scraper;

import wow.scraper.exporter.item.*;
import wow.scraper.exporter.item.excel.*;
import wow.scraper.parser.WowheadSourceParser;
import wow.scraper.parser.tooltip.AbstractTooltipParser;

import static wow.scraper.model.WowheadItemCategory.EquipmentGroup.*;

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
		export("item/items-cloth.xls", ItemExcelBuilder::new, new ItemExporter(CLOTH_ARMOR));
		export("item/items-leather.xls", ItemExcelBuilder::new, new ItemExporter(LEATHER_ARMOR));
		export("item/items-mail.xls", ItemExcelBuilder::new, new ItemExporter(MAIL_ARMOR));
		export("item/items-plate.xls", ItemExcelBuilder::new, new ItemExporter(PLATE_ARMOR));
		export("item/items-weapons.xls", ItemExcelBuilder::new, new ItemExporter(WEAPONS));
		export("item/items-misc.xls", ItemExcelBuilder::new, new ItemExporter(MISC));
		export("item/item-sets.xls", ItemSetExcelBuilder::new, new ItemSetExporter());
		export("item/enchants.xls", EnchantExcelBuilder::new, new EnchantExporter());
		export("item/gems.xls", GemExcelBuilder::new, new GemItemExporter());

		AbstractTooltipParser.reportUnmatchedLines();
	}
}
