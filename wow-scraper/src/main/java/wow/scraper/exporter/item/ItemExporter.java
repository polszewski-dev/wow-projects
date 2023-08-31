package wow.scraper.exporter.item;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.exporter.item.excel.ItemBaseExcelBuilder;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.parser.tooltip.ItemTooltipParser;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class ItemExporter extends AbstractItemExporter<ItemTooltipParser> {
	@Override
	protected void prepareData() {
		for (WowheadItemCategory category : WowheadItemCategory.equipment()) {
			export(category);
		}
	}

	@Override
	protected void exportPreparedData(ItemBaseExcelBuilder builder) {
		builder.addItemHeader();
		parsers.forEach(builder::add);
	}

	@Override
	protected ItemTooltipParser createParser(JsonItemDetails details, GameVersionId gameVersion) {
		return new ItemTooltipParser(details, gameVersion, getScraperContext());
	}
}
