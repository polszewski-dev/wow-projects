package wow.scraper.exporters.item;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.model.JsonItemDetails;
import wow.scraper.model.WowheadItemCategory;
import wow.scraper.parsers.tooltip.ItemTooltipParser;

import java.io.IOException;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class ItemExporter extends ItemBaseExporter<ItemTooltipParser> {
	@Override
	public void exportAll() throws IOException {
		builder.addItemHeader();

		for (WowheadItemCategory category : WowheadItemCategory.equipment()) {
			export(category);
		}
	}

	@Override
	protected ItemTooltipParser createParser(JsonItemDetails itemDetails, GameVersionId gameVersion) {
		return new ItemTooltipParser(itemDetails, gameVersion, statPatternRepository);
	}

	@Override
	protected void exportParsedData(ItemTooltipParser parser) {
		builder.add(parser);
	}
}
