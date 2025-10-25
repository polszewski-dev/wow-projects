package wow.scraper.exporter.item;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.item.excel.ItemExcelBuilder;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.parser.tooltip.ItemTooltipParser;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class ItemExporter extends AbstractItemExporter<ItemTooltipParser, ItemExcelBuilder> {
	@Override
	protected void prepareData() {
		for (WowheadItemCategory category : WowheadItemCategory.equipment()) {
			export(category);
		}
	}

	@Override
	protected void exportPreparedData(ItemExcelBuilder builder) {
		builder.addItemHeader();
		parsers.forEach(builder::add);
		parsers.forEach(ItemSetExporter.SAVED_SETS::save);
	}

	@Override
	protected ItemTooltipParser createParser(JsonItemDetails details, GameVersionId gameVersion) {
		return new ItemTooltipParser(details, gameVersion, getScraperContext());
	}
}
